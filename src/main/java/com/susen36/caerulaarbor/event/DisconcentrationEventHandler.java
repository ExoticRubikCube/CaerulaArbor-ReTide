package com.susen36.caerulaarbor.event;

import com.susen36.babel.difficulty.NDifficulty;
import com.susen36.caerulaarbor.CaerulaArbor;
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
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class DisconcentrationEventHandler {
    private static final int NO_REJECTION_STAGE = 0;
    private static final int DISCONCENTRATION_REJECTION_STAGE = 1;
    private static final int HAEMOPHILIA_REJECTION_STAGE = 2;
    private static final int NEURODEGENERATION_REJECTION_STAGE = 3;
    private static final int FLESHDEFORMITY_REJECTION_STAGE = 4;

    // 组合排异值域（5-10），对应 wiki 的 6 种固定组合（命名 X and Y，X 为左、Y 为右）
    private static final int NEURO_ATTENTION_COMBO_STAGE = 5;   // Degeneration and Disorder
    private static final int NEURO_BLOOD_COMBO_STAGE = 6;       // Degeneration and Inhibition
    private static final int NEURO_FLESH_COMBO_STAGE = 7;       // Degeneration and Aberration
    private static final int ATTENTION_BLOOD_COMBO_STAGE = 8;   // Disorder and Inhibition
    private static final int ATTENTION_FLESH_COMBO_STAGE = 9;   // Disorder and Aberration
    private static final int BLOOD_FLESH_COMBO_STAGE = 10;      // Inhibition and Aberration
    private static final int FIRST_COMBO_STAGE = NEURO_ATTENTION_COMBO_STAGE;
    private static final int LAST_COMBO_STAGE = BLOOD_FLESH_COMBO_STAGE;

    // ---- 深度排异 (半海嗣化, 1<=oceanization<3) 特有参数 ----
    // 造血障碍放大器：基础 amp1(每40tick掉5%)，深度 amp2(每40tick掉7.5%)
    private static final int HAEMOPHILIA_AMPLIFIER = 1;
    private static final int DEEP_HAEMOPHILIA_AMPLIFIER = 2;
    // 血肉畸变削弱修正（由 DisconcentrationEventHandler 动态施加）：基础各-25%、深度各-50%
    private static final ResourceLocation FLESHDEFORMITY_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_armor");
    private static final ResourceLocation FLESHDEFORMITY_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_max_health");
    private static final ResourceLocation FLESHDEFORMITY_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_attack_damage");

    // 深度专注失调发作期：持续 100tick(5s)，攻速-10%、攻击+25%
    private static final int DISORDER_FLARE_DURATION = 100;
    private static final ResourceLocation DISORDER_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_disorder_attack_speed");
    private static final ResourceLocation DISORDER_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_disorder_attack_damage");

    // 深度神经退行受击加成：攻击+80%、护甲+100%，持续等同冻结时长
    private static final ResourceLocation NEURO_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_neuro_attack_damage");
    private static final ResourceLocation NEURO_ARMOR_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_neuro_armor");

    // 深度血肉畸变补偿：攻速+40%、移速+15%、游泳+20%
    private static final ResourceLocation DEEP_FLESHDEFORMITY_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_attack_speed");
    private static final ResourceLocation DEEP_FLESHDEFORMITY_MOVE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_move_speed");
    private static final ResourceLocation DEEP_FLESHDEFORMITY_SWIM_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_flesh_swim_speed");

    // 深度造血障碍低血增伤（动态量随血量变化）
    private static final ResourceLocation HAEM_LOW_HEALTH_BONUS_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "deep_haem_low_health_attack");

    // 服务端瞬态跨期计数（以 tick 计）
    private static final Map<UUID, Integer> DISORDER_FLARE_REMAINING = new HashMap<>();
    private static final Map<UUID, Integer> NEURO_BUFF_REMAINING = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.tickCount % 10 != 0) {
            return;
        }
        double rejectionStage = getRejectionStage(player);

        // 满海嗣化(>=3)免疫排异负面效果：保留排异状态供背景/记录展示，本 tick 置零以跳过全部效果施加
        PlayerVariable playerVariables = getPlayerVariables(player);
        if (playerVariables.player_oceanization >= 3) {
            rejectionStage = NO_REJECTION_STAGE;
        }

        // 深度排异判定：半海嗣化(1<=oceanization<3)的单个排异升级为对应深度版
        boolean deepConcern = isSemiOceanized(player) && rejectionStage == DISCONCENTRATION_REJECTION_STAGE;
        boolean deepHaem = isSemiOceanized(player) && rejectionStage == HAEMOPHILIA_REJECTION_STAGE;
        boolean deepNeuro = isSemiOceanized(player) && rejectionStage == NEURODEGENERATION_REJECTION_STAGE;
        boolean deepFlesh = isSemiOceanized(player) && rejectionStage == FLESHDEFORMITY_REJECTION_STAGE;

        // 高难度（>=17）下已有的单个排异直接进化为组合排异（原反应固定为左位主反应 + 随机右位反应）；半海嗣化不参与组合，只保留单个深度排异
        if (rejectionStage >= DISCONCENTRATION_REJECTION_STAGE && rejectionStage <= FLESHDEFORMITY_REJECTION_STAGE
                && !deepConcern && !deepHaem && !deepNeuro && !deepFlesh
                && isHighDifficulty(player.level()) && !player.level().isClientSide()) {
            PlayerVariable capability = getPlayerVariables(player);
            capability.disoclusion = evolveToComposite((int) rejectionStage);
            capability.syncPlayerVariables(player);
            rejectionStage = capability.disoclusion;
            player.displayClientMessage(Component.literal(Component.translatable("item.caerula_arbor.rejection_key.description_0").getString()
                    + Component.translatable("item.caerula_arbor.rejection_key.description_" + (int) capability.disoclusion).getString()), false);
        }
        // 低难度（<17）下已有的组合排异退化回左位主反应
        else if (rejectionStage >= FIRST_COMBO_STAGE && rejectionStage <= LAST_COMBO_STAGE
                && !isHighDifficulty(player.level()) && !player.level().isClientSide()) {
            PlayerVariable capability = getPlayerVariables(player);
            capability.disoclusion = revertToPrimary((int) rejectionStage);
            capability.syncPlayerVariables(player);
            rejectionStage = capability.disoclusion;
            player.displayClientMessage(Component.literal(Component.translatable("item.caerula_arbor.rejection_key.description_0").getString()
                    + Component.translatable("item.caerula_arbor.rejection_key.description_" + (int) capability.disoclusion).getString()), false);
        }

        // 造血障碍（持续扣血）：单个(2)或含血(6/8/10)时附加（深度 amp2=每40tick掉7.5%），否则移除
        int haemAmp = deepHaem ? DEEP_HAEMOPHILIA_AMPLIFIER : HAEMOPHILIA_AMPLIFIER;
        if (rejectionStage == HAEMOPHILIA_REJECTION_STAGE || rejectionStage == NEURO_BLOOD_COMBO_STAGE
                || rejectionStage == ATTENTION_BLOOD_COMBO_STAGE || rejectionStage == BLOOD_FLESH_COMBO_STAGE) {
            MobEffectInstance currentHaem = player.getEffect(CAMobEffects.HAEMOPHILIA);
            if (!player.level().isClientSide() && (currentHaem == null || currentHaem.getAmplifier() != haemAmp)) {
                player.removeEffect(CAMobEffects.HAEMOPHILIA);
                player.addEffect(new MobEffectInstance(CAMobEffects.HAEMOPHILIA, -1, haemAmp, false, false));
            }
        } else if (player.hasEffect(CAMobEffects.HAEMOPHILIA)) {
            player.removeEffect(CAMobEffects.HAEMOPHILIA);
        }

        // 深度造血障碍低血增伤：每缺失10%血量+10%攻击（生命值低于75%生效），动态随血量变化
        if (deepHaem && !player.level().isClientSide()) {
            double maxHealth = player.getMaxHealth();
            double healthPercent = maxHealth <= 0.0 ? 1.0 : player.getHealth() / maxHealth;
            double missingBlocks = healthPercent < 0.75 ? Mth.floor((1.0 - healthPercent) * 10.0) : 0;
            if (missingBlocks > 0) {
                setModifier(player, Attributes.ATTACK_DAMAGE, HAEM_LOW_HEALTH_BONUS_ID, missingBlocks * 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            } else {
                removeModifier(player, Attributes.ATTACK_DAMAGE, HAEM_LOW_HEALTH_BONUS_ID);
            }
        } else if (!player.level().isClientSide()) {
            removeModifier(player, Attributes.ATTACK_DAMAGE, HAEM_LOW_HEALTH_BONUS_ID);
        }

        // 血肉畸变：单个(4)或含肉(7/9/10)时生效；削弱与补偿均由本handler动态施加（基础各-25%，深度各-50%+攻速/移速/游泳补偿），否则移除
        if (rejectionStage == FLESHDEFORMITY_REJECTION_STAGE || rejectionStage == NEURO_FLESH_COMBO_STAGE
                || rejectionStage == ATTENTION_FLESH_COMBO_STAGE || rejectionStage == BLOOD_FLESH_COMBO_STAGE) {
            if (!player.level().isClientSide()) {
                if (deepFlesh) {
                    setModifier(player, Attributes.ARMOR, FLESHDEFORMITY_ARMOR_ID, -0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    setModifier(player, Attributes.MAX_HEALTH, FLESHDEFORMITY_MAX_HEALTH_ID, -0.50, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    setModifier(player, Attributes.ATTACK_DAMAGE, FLESHDEFORMITY_ATTACK_DAMAGE_ID, -0.50, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    setModifier(player, Attributes.ATTACK_SPEED, DEEP_FLESHDEFORMITY_ATTACK_SPEED_ID, 0.40, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    setModifier(player, Attributes.MOVEMENT_SPEED, DEEP_FLESHDEFORMITY_MOVE_SPEED_ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    setModifier(player, NeoForgeMod.SWIM_SPEED, DEEP_FLESHDEFORMITY_SWIM_SPEED_ID, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                } else {
                    setModifier(player, Attributes.ARMOR, FLESHDEFORMITY_ARMOR_ID, -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    setModifier(player, Attributes.MAX_HEALTH, FLESHDEFORMITY_MAX_HEALTH_ID, -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                    setModifier(player, Attributes.ATTACK_DAMAGE, FLESHDEFORMITY_ATTACK_DAMAGE_ID, -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    removeModifier(player, Attributes.ATTACK_SPEED, DEEP_FLESHDEFORMITY_ATTACK_SPEED_ID);
                    removeModifier(player, Attributes.MOVEMENT_SPEED, DEEP_FLESHDEFORMITY_MOVE_SPEED_ID);
                    removeModifier(player, NeoForgeMod.SWIM_SPEED, DEEP_FLESHDEFORMITY_SWIM_SPEED_ID);
                }
            }
        } else {
            if (!player.level().isClientSide()) {
                removeModifier(player, Attributes.ARMOR, FLESHDEFORMITY_ARMOR_ID);
                removeModifier(player, Attributes.MAX_HEALTH, FLESHDEFORMITY_MAX_HEALTH_ID);
                removeModifier(player, Attributes.ATTACK_DAMAGE, FLESHDEFORMITY_ATTACK_DAMAGE_ID);
                removeModifier(player, Attributes.ATTACK_SPEED, DEEP_FLESHDEFORMITY_ATTACK_SPEED_ID);
                removeModifier(player, Attributes.MOVEMENT_SPEED, DEEP_FLESHDEFORMITY_MOVE_SPEED_ID);
                removeModifier(player, NeoForgeMod.SWIM_SPEED, DEEP_FLESHDEFORMITY_SWIM_SPEED_ID);
            }
        }

        // 服务端瞬态跨期：深度专注失调发作期（攻速-10%、攻击+25%，持续5s）与深度神经退行受击加成（攻击+80%、护甲+100%）
        if (!player.level().isClientSide()) {
            UUID playerId = player.getUUID();

            Integer disorderRemain = DISORDER_FLARE_REMAINING.get(playerId);
            if (disorderRemain != null && disorderRemain > 0) {
                setModifier(player, Attributes.ATTACK_SPEED, DISORDER_ATTACK_SPEED_ID, -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                setModifier(player, Attributes.ATTACK_DAMAGE, DISORDER_ATTACK_DAMAGE_ID, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                int remaining = disorderRemain - 10;
                if (remaining <= 0) {
                    DISORDER_FLARE_REMAINING.remove(playerId);
                    removeModifier(player, Attributes.ATTACK_SPEED, DISORDER_ATTACK_SPEED_ID);
                    removeModifier(player, Attributes.ATTACK_DAMAGE, DISORDER_ATTACK_DAMAGE_ID);
                } else {
                    DISORDER_FLARE_REMAINING.put(playerId, remaining);
                }
            } else {
                removeModifier(player, Attributes.ATTACK_SPEED, DISORDER_ATTACK_SPEED_ID);
                removeModifier(player, Attributes.ATTACK_DAMAGE, DISORDER_ATTACK_DAMAGE_ID);
                if (deepConcern && player.tickCount % 20 == 0 && Math.random() < 0.15) {
                    DISORDER_FLARE_REMAINING.put(playerId, DISORDER_FLARE_DURATION);
                }
            }

            Integer neuroRemain = NEURO_BUFF_REMAINING.get(playerId);
            if (neuroRemain != null && neuroRemain > 0) {
                setModifier(player, Attributes.ATTACK_DAMAGE, NEURO_ATTACK_DAMAGE_ID, 0.80, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                setModifier(player, Attributes.ARMOR, NEURO_ARMOR_ID, 1.00, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                int remaining = neuroRemain - 10;
                if (remaining <= 0) {
                    NEURO_BUFF_REMAINING.remove(playerId);
                    removeModifier(player, Attributes.ATTACK_DAMAGE, NEURO_ATTACK_DAMAGE_ID);
                    removeModifier(player, Attributes.ARMOR, NEURO_ARMOR_ID);
                } else {
                    NEURO_BUFF_REMAINING.put(playerId, remaining);
                }
            } else {
                removeModifier(player, Attributes.ATTACK_DAMAGE, NEURO_ATTACK_DAMAGE_ID);
                removeModifier(player, Attributes.ARMOR, NEURO_ARMOR_ID);
            }
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

        double rejectionStage = getRejectionStage(player);
        // 神经退行：单个(3)或含神经(5/6/7)时受击有几率冻结
        if (rejectionStage != NEURODEGENERATION_REJECTION_STAGE && rejectionStage != NEURO_ATTENTION_COMBO_STAGE
                && rejectionStage != NEURO_BLOOD_COMBO_STAGE && rejectionStage != NEURO_FLESH_COMBO_STAGE) {
            return;
        }

        boolean deepNeuro = isSemiOceanized(player) && rejectionStage == NEURODEGENERATION_REJECTION_STAGE;
        double freezeChance = 0.04;
        int freezeDuration = 40;
        float biomeTemperature = player.level().getBiome(BlockPos.containing(player.getX(), player.getY(), player.getZ())).value().getBaseTemperature() * 100.0F;
        if (deepNeuro) {
            // 深度神经退行：冻结时长 热带60 / 常规80 / 寒带120，并在触发时附带攻防加成（持续等同冻结时长）
            if (biomeTemperature >= 180.0F) {
                freezeChance = 0.01;
                freezeDuration = 60;
            } else if (biomeTemperature <= 10.0F) {
                freezeChance = 0.06;
                freezeDuration = 120;
            } else {
                freezeDuration = 80;
            }
        } else if (biomeTemperature >= 180.0F) {
            freezeChance = 0.01;
            freezeDuration = 20;
        } else if (biomeTemperature <= 10.0F) {
            freezeChance = 0.06;
            freezeDuration = 80;
        }

        if (Math.random() < freezeChance && !player.level().isClientSide()) {
            player.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, freezeDuration, 0, false, false));
            if (deepNeuro) {
                NEURO_BUFF_REMAINING.put(player.getUUID(), freezeDuration);
            }
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

        if (!sourceEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
            return;
        }

        double playerLight = playerVariables.player_light;
        if (playerLight >= 85 || playerVariables.disoclusion != NO_REJECTION_STAGE) {
            return;
        }

        double damagePercent = Math.min(event.getNewDamage() / player.getMaxHealth(), 1.0);
        double rejectionChance = 0.02 * damagePercent;
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

        int rejectionStage = rollRejectionStage(player.level());
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
            AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_we_many"));
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
            ItemStack armorItem = livingEntity.getItemBySlot(new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}[armorSlotIndex]).copy();
            if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.REJECTION_CURSE), armorItem) != 0) {
                return true;
            }
        }
        return false;
    }

    private static double getRejectionStage(Entity entity) {
        return getPlayerVariables(entity).disoclusion;
    }

    /** 当前世界是否为高难度（Surging Waves 难度 >= 17，此时排异进化为组合排异）。 */
    private static boolean isHighDifficulty(LevelAccessor world) {
        return NDifficulty.difficultyLevel(world).value() >= 17;
    }

    /** 授予排异反应：高难度授予随机组合排异（5-10），否则授予随机单个排异（1-4）。 */
    public static int rollRejectionStage(LevelAccessor world) {
        if (isHighDifficulty(world)) {
            return Mth.nextInt(RandomSource.create(), FIRST_COMBO_STAGE, LAST_COMBO_STAGE);
        }
        return Mth.nextInt(RandomSource.create(), DISCONCENTRATION_REJECTION_STAGE, FLESHDEFORMITY_REJECTION_STAGE);
    }

    /** 单个排异（1-4）进化为组合排异（5-10）：原反应固定为左位主反应，另一反应随机为右位；血肉畸变无左位组合，恒为右位。 */
    private static int evolveToComposite(int singleStage) {
        if (singleStage == DISCONCENTRATION_REJECTION_STAGE) {
            return randomOf(ATTENTION_BLOOD_COMBO_STAGE, ATTENTION_FLESH_COMBO_STAGE);
        } else if (singleStage == HAEMOPHILIA_REJECTION_STAGE) {
            return BLOOD_FLESH_COMBO_STAGE;
        } else if (singleStage == NEURODEGENERATION_REJECTION_STAGE) {
            return randomOf(NEURO_ATTENTION_COMBO_STAGE, NEURO_BLOOD_COMBO_STAGE, NEURO_FLESH_COMBO_STAGE);
        } else {
            return randomOf(NEURO_FLESH_COMBO_STAGE, ATTENTION_FLESH_COMBO_STAGE, BLOOD_FLESH_COMBO_STAGE);
        }
    }

    /** 组合排异（5-10）退化回左位主反应（1-4）。 */
    private static int revertToPrimary(int comboStage) {
        if (comboStage == NEURO_ATTENTION_COMBO_STAGE || comboStage == NEURO_BLOOD_COMBO_STAGE || comboStage == NEURO_FLESH_COMBO_STAGE) {
            return NEURODEGENERATION_REJECTION_STAGE;
        } else if (comboStage == ATTENTION_BLOOD_COMBO_STAGE || comboStage == ATTENTION_FLESH_COMBO_STAGE) {
            return DISCONCENTRATION_REJECTION_STAGE;
        } else {
            return HAEMOPHILIA_REJECTION_STAGE;
        }
    }

    /** 从给定的一组排异阶段中随机返回一个。 */
    private static int randomOf(int... stages) {
        return stages[Mth.nextInt(RandomSource.create(), 0, stages.length - 1)];
    }

    /** 玩家是否为半海嗣化（启蒙1 / 顿悟2，即 oceanization 在 [1, 3) 区间）：此时单个排异升级为深度排异，不参与组合。 */
    private static boolean isSemiOceanized(Entity entity) {
        int oceanization = getPlayerVariables(entity).player_oceanization;
        return oceanization >= 1 && oceanization < 3;
    }

    /** 为实体施加（或更新）一个瞬态属性修正；已存在同 ID 修正时直接替换，避免重复叠加。 */
    private static void setModifier(Entity entity, Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance instance = livingEntity.getAttribute(attribute);
            if (instance != null) {
                AttributeModifier modifier = new AttributeModifier(id, amount, operation);
                AttributeModifier existing = instance.getModifier(id);
                if (existing == null) {
                    instance.addTransientModifier(modifier);
                } else {
                    instance.removeModifier(id);
                    instance.addTransientModifier(modifier);
                }
            }
        }
    }

    /** 移除实体上指定 ID 的瞬态属性修正（若存在）。 */
    private static void removeModifier(Entity entity, Holder<Attribute> attribute, ResourceLocation id) {
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance instance = livingEntity.getAttribute(attribute);
            if (instance != null && instance.getModifier(id) != null) {
                instance.removeModifier(id);
            }
        }
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

            double rejectionStage = getRejectionStage(localPlayer);
            // 专注失调：单个(1)或含专注(5/8/9)时随机触发按键
            if ((rejectionStage != DISCONCENTRATION_REJECTION_STAGE && rejectionStage != NEURO_ATTENTION_COMBO_STAGE
                    && rejectionStage != ATTENTION_BLOOD_COMBO_STAGE && rejectionStage != ATTENTION_FLESH_COMBO_STAGE)
                    || localPlayer.level().random.nextDouble() > 0.1) {
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