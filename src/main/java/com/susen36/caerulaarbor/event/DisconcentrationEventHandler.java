package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.advancements.AdvancementHolder;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class DisconcentrationEventHandler {
    private static final int NO_REJECTION_STAGE = 0;
    private static final int DISCONCENTRATION_REJECTION_STAGE = 1;
    private static final int HAEMOPHILIA_REJECTION_STAGE = 2;
    private static final int NEURODEGENERATION_REJECTION_STAGE = 3;
    private static final int FLESHDEFORMITY_REJECTION_STAGE = 4;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.tickCount % 10 != 0) {
            return;
        }
        double rejectionStage = getRejectionStage(player);
        if (rejectionStage == HAEMOPHILIA_REJECTION_STAGE) {
            if (!player.hasEffect(CAMobEffects.HAEMOPHILIA) && !player.level().isClientSide()) {
                player.addEffect(new MobEffectInstance(CAMobEffects.HAEMOPHILIA, -1, 1, false, false));
            }
        } else {
            player.removeEffect(CAMobEffects.HAEMOPHILIA);
        }

        if (rejectionStage == FLESHDEFORMITY_REJECTION_STAGE) {
            if (!player.hasEffect(CAMobEffects.FLESHDEFORMITY) && !player.level().isClientSide()) {
                player.addEffect(new MobEffectInstance(CAMobEffects.FLESHDEFORMITY, -1, 1, false, false));
            }
        } else if (player.hasEffect(CAMobEffects.FLESHDEFORMITY)) {
            player.removeEffect(CAMobEffects.FLESHDEFORMITY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
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
            player.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, freezeDuration, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        Entity targetEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();
        if (!(targetEntity instanceof Player player) || sourceEntity == null) {
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

        double damagePercent = Math.min(event.getNewDamage() / player.getMaxHealth(), 1.0);
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
        PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
        capability.disoclusion = rejectionStage;
        capability.syncPlayerVariables(player);

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
            AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_we_many"));
            AdvancementProgress advancementProgress = null;
            if (advancement != null) {
                advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            }
            if (advancementProgress != null && !advancementProgress.isDone()) {
                for (String criteria : advancementProgress.getRemainingCriteria()) {
                    serverPlayer.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    private static PlayerVariable getPlayerVariables(Entity entity) {
        return ModCapabilities.getPlayerVariables(entity);
    }

    private static boolean hasRejectionCurseArmor(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        for (int armorSlotIndex = 0; armorSlotIndex < 4; armorSlotIndex++) {
            ItemStack armorItem = livingEntity.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, armorSlotIndex)).copy();
            if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.REJECTION_CURSE), armorItem) != 0) {
                return true;
            }
        }
        return false;
    }

    private static double getRejectionStage(Entity entity) {
        return getPlayerVariables(entity).disoclusion;
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {

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