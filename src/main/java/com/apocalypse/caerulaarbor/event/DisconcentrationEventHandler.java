package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAEnchantments;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DisconcentrationEventHandler {
    private static final int NO_REJECTION_STAGE = 0;
    private static final int DISCONCENTRATION_REJECTION_STAGE = 1;
    private static final int HAEMOPHILIA_REJECTION_STAGE = 2;
    private static final int NEURODEGENERATION_REJECTION_STAGE = 3;
    private static final int FLESHDEFORMITY_REJECTION_STAGE = 4;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player == null || event.player.tickCount % 10 != 0) {
            return;
        }

        Player player = event.player;
        double rejectionStage = getRejectionStage(player);
        if (rejectionStage == HAEMOPHILIA_REJECTION_STAGE) {
            if (!player.hasEffect(CAMobEffects.HAEMOPHILIA.get()) && !player.level().isClientSide()) {
                player.addEffect(new MobEffectInstance(CAMobEffects.HAEMOPHILIA.get(), 10000, 1, false, false));
            }
        } else {
            player.removeEffect(CAMobEffects.HAEMOPHILIA.get());
        }

        if (rejectionStage == FLESHDEFORMITY_REJECTION_STAGE) {
            if (!player.level().isClientSide()) {
                player.addEffect(new MobEffectInstance(CAMobEffects.FLESHDEFORMITY.get(), 999, 1, false, false));
            }
        } else if (player.hasEffect(CAMobEffects.FLESHDEFORMITY.get())) {
            // TODO：待向原作者确认。待移植文件这里移除的是 HAEMOPHILIA，而不是 FLESHDEFORMITY，当前先保留原行为。
            player.removeEffect(CAMobEffects.HAEMOPHILIA.get());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity targetEntity = event.getEntity();
        Entity directSourceEntity = event.getSource().getDirectEntity();
        Entity sourceEntity = event.getSource().getEntity();
        if (!(targetEntity instanceof Player player) || directSourceEntity == null || sourceEntity == null || event.isCanceled()) {
            return;
        }

        if (getRejectionStage(player) != NEURODEGENERATION_REJECTION_STAGE) {
            return;
        }

        double freezeChance = 0.04;
        int freezeDuration = 40;
        float biomeTemperature = player.level().getBiome(BlockPos.containing(player.getX(), player.getY(), player.getZ())).value().getBaseTemperature() * 100.0F;
        if (biomeTemperature >= 180.0F) {
            freezeChance = 0.01;
            freezeDuration = 20;
        } else if (biomeTemperature <= 10.0F) {
            freezeChance = 0.06;
            freezeDuration = 80;
        }

        if (Math.random() < freezeChance && !player.level().isClientSide()) {
            player.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), freezeDuration, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        Entity targetEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();
        if (!(targetEntity instanceof Player player) || sourceEntity == null || event.isCanceled()) {
            return;
        }

        PlayerVariable playerVariables = getPlayerVariables(player);
        if (playerVariables.player_oceanization > 2) {
            return;
        }

        if (!sourceEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
            return;
        }

        double playerLight = playerVariables.player_light;
        if (playerLight >= 85 || playerVariables.disoclusion != NO_REJECTION_STAGE) {
            return;
        }

        double damagePercent = Math.min(event.getAmount() / player.getMaxHealth(), 1.0);
        double rejectionChance = 0.0025 + 0.0125 * damagePercent;
        if (playerLight < 1) {
            rejectionChance = rejectionChance * 2;
        } else if (playerLight >= 50) {
            rejectionChance = rejectionChance * 0.5;
        }
        if (hasRejectionCurseArmor(player)) {
            rejectionChance = rejectionChance * 2;
        }
        if (Math.random() >= rejectionChance) {
            return;
        }

        int rejectionStage = Mth.nextInt(RandomSource.create(), DISCONCENTRATION_REJECTION_STAGE, FLESHDEFORMITY_REJECTION_STAGE);
        player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
            capability.disoclusion = rejectionStage;
            capability.syncPlayerVariables(player);
        });

        Level level = player.level();
        if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1, 1);
        } else {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1, 1, false);
        }

        if (!player.level().isClientSide()) {
            player.displayClientMessage(
                    Component.literal(Component.translatable("item.caerula_arbor.rejection_key.description_0").getString()
                            + Component.translatable("item.caerula_arbor.rejection_key.description_" + rejectionStage).getString()),
                    false);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_we_many"));
            AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            if (!advancementProgress.isDone()) {
                for (String criteria : advancementProgress.getRemainingCriteria()) {
                    serverPlayer.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    private static PlayerVariable getPlayerVariables(Entity entity) {
        return entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
                .orElse(new PlayerVariable());
    }

    private static boolean hasRejectionCurseArmor(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        for (int armorSlotIndex = 0; armorSlotIndex < 4; armorSlotIndex++) {
            ItemStack armorItem = livingEntity.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, armorSlotIndex)).copy();
            if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.REJECTION_CURSE.get(), armorItem) != 0) {
                return true;
            }
        }
        return false;
    }

    private static double getRejectionStage(Entity entity) {
        return getPlayerVariables(entity).disoclusion;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                return;
            }

            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            if (localPlayer == null || !localPlayer.isAlive() || localPlayer.tickCount % 20 != 0) {
                return;
            }

            if (getRejectionStage(localPlayer) != DISCONCENTRATION_REJECTION_STAGE || localPlayer.level().random.nextDouble() > 0.1) {
                return;
            }

            if (localPlayer.level().random.nextDouble() > 0.5) {
                KeyMapping.click(minecraft.options.keyUse.getKey());
            } else {
                KeyMapping.click(minecraft.options.keyAttack.getKey());
            }
        }
    }
}
