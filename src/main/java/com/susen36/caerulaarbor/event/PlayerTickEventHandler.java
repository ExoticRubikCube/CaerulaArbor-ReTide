package com.susen36.caerulaarbor.event;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.manager.EPManager;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.NodeUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import com.susen36.caerulaarbor.util.RelicUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber
public class PlayerTickEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        handleEssenceResistanceWithIce(player);
        handleHandSwipeFunc(player);
        handlePlayerEvolutionTick(player);
        handlePlayerTickFunc(player);
    }

    private static void handleEssenceResistanceWithIce(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 2 == 0 && entity.hasEffect(BabelMobEffects.ESSENCE_RESISTANCE)) {
            if (entity.getTicksFrozen() < 140) entity.setTicksFrozen(Math.max(entity.getTicksFrozen() - 1, 0));
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks() - 1, 0));
        }
    }

    private static void handleHandSwipeFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 20 != 0) return;

        if (CARelics.HAND_SWIPE.get().gained(entity)) {
            if (!((entity.getOffhandItem()).getItem() == net.minecraft.world.item.Items.BRUSH)) return;

            if (entity.hasEffect(MobEffects.REGENERATION)) {
                LevelAccessor world = entity.level();
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();

                final Vec3 center = new Vec3(x, y, z);
                List<net.minecraft.world.entity.monster.Monster> entfound = world.getEntitiesOfClass(net.minecraft.world.entity.monster.Monster.class, new AABB(center, center).inflate(16 / 2d), e -> true);
                for (net.minecraft.world.entity.monster.Monster entityiterator : entfound) {
                    if (entity.distanceToSqr(entityiterator) < 64) {
                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.WIPE_MAGIC, entity), (float) (5 * (1 + (entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0))));
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.WAX_OFF, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 12, 0.8, 1, 0.8, 0.1);
                    }
                }
            }
        }
    }

    private static void handlePlayerEvolutionTick(Player entity) {
        if (entity == null) return;
        if (!EntityUtils.canPlayerEvo(entity)) return;

        double tickCount = entity.tickCount;

        if (PlayerStateUtils.isNexusNoRejectionSelected(entity)) {
            if (RelicUtils.hasRelic(CARelics.DISO.get(), entity)) {
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                capability.disoclusion = -1;
                capability.syncPlayerVariables(entity);
            }
        }

        if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
            EPManager.getEP(entity).getEP(AbstractEPCapability.EPType.NERVOUS).heal(1);
        }

        if (tickCount % 10 == 0) {
            handlePlayerEvolutionTickBuffs(entity);
        }
    }

    private static void handlePlayerEvolutionTickBuffs(Player entity) {
        if (PlayerStateUtils.isNexusRegLightsSelected(entity)) {
            EntityUtils.restorePlayerLights(entity, 0.1);
        }

        double addDef = NodeUtils.getNodeAddDef(entity);
        if (addDef > 0) {
            if (!entity.level().isClientSide()) {
                if (addDef == 1) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY, 20, 0, false, false));
                } else if (addDef == 2) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY, 20, 2, false, false));
                } else if (addDef == 3) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY, 20, 5, false, false));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_PERCLY_TINY, 20, 2, false, false));
                } else if (addDef == 4) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY, 20, 9, false, false));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_PERCLY_TINY, 20, 7, false, false));
                }
            }
        }

        addDef = NodeUtils.getNodeAddResis(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY, 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY, 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY, 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY, 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddSpeed(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY, 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY, 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY, 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY, 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddSanity(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER, 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER, 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER, 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER, 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddMiss(entity);
        boolean lowerHealth = EntityUtils.getHealthPerc(entity) <= 0.5;
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE, 20, 2, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE, 20, 7, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE, 20, lowerHealth ? 22 : 14, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE, 20, lowerHealth ? 41 : 23, false, false));
        }

        addDef = NodeUtils.getNodeEunectes(entity);
        if (addDef > 0 && !lowerHealth && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY, 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY, 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY, 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY, 20, 9, false, false));
        }
    }

    private static void handlePlayerTickFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 10 != 0) return;

        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        handleSanityModifier(entity);
        handleKingSuit(entity);
        handleHandSpeed(entity, world, x, y, z);
        handleArchfiSuit(entity);
        handleEngraveAndSurvivor(entity);
        handlePlayerLives(entity);
        handleSanityDefendEnchant(entity);
        handleOceanizationEffects(entity);
        handleRelicHemost(entity);
        handleRelicYearning(entity);
    }

    private static void handleSanityModifier(Player entity) {
        double modifi = 1;
        if (PlayerStateUtils.isLightDim(entity)) {
            modifi = 1.2;
        } else if (PlayerStateUtils.isLightCeased(entity)) {
            modifi = 1.5;
        }
        if (ModCapabilities.getPlayerVariables(entity).player_oceanization >= 3) {
            modifi = modifi * 0.33;
        }
        EPManager.setElementalDefenseBaseModifier(entity, modifi);
    }

    private static void handleKingSuit(Player entity) {
        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
        if (capability.player_lives > 1) return;

        double suitKing = 0;
        if (CARelics.KING_SPEAR.get().gained(capability)) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BOOST, 20, 1, false, false));
        }
        if (CARelics.KING_ARMOR.get().gained(capability)) {
            suitKing = suitKing + 1;
        }
        if (CARelics.KING_EXTENSION.get().gained(capability)) {
            suitKing = suitKing + 1;
            double amplifi = Math.ceil(entity.getMaxHealth() / 20);
            if (amplifi > 24) amplifi = 24;
            if ((entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < amplifi) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (int) amplifi, false, false));
            }
        }
        if (CARelics.KING_CROWN.get().gained(capability)) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BREATH, 20, suitKing < 3 ? 0 : 2, false, false));
            }
            capability.player_king_suit = suitKing < 3 ? 1 : 2;
            capability.syncPlayerVariables(entity);
        } else {
            capability.player_king_suit = 0;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleHandSpeed(Player entity, LevelAccessor world, double x, double y, double z) {
        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if (CARelics.HAND_SPEED.get().gained(entity)) {
            if (mainHandItem.getItem() instanceof PickaxeItem || mainHandItem.is(ItemTags.create(ResourceLocation.parse("minecraft:pickaxes")))) {
                boolean valid = true;
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntities(entity, new AABB(center, center).inflate(8 / 2d));
                for (Entity entityiterator : entfound) {
                    if (entityiterator == entity) continue;
                    if (entityiterator instanceof Player || entityiterator instanceof Animal) {
                        valid = false;
                        break;
                    }
                }
                if (valid && !entity.level().isClientSide()) {
                    if ((entity.hasEffect(MobEffects.DIG_SPEED) ? entity.getEffect(MobEffects.DIG_SPEED).getAmplifier() : 0) < 2) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2));
                    }
                    entity.addEffect(new MobEffectInstance(CAMobEffects.HANDS_SPEED, 20, 2));
                }
            }
        }
    }

    private static void handleArchfiSuit(Player entity) {
        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
        if (capability.player_lives < capability.player_maxlive)
            return;

        double suitArchfi = 0;
        if (CARelics.SARKAZ_KING_FLAG.get().gained(capability)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.FLAG_SWINGS, 20, 2, false, false));
        }
        if (CARelics.SARKAZ_KING_BED.get().gained(capability)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KEEP_BEDDING, 20, 0, false, false));
        }
        if (CARelics.SARKAZ_KING_ARTIFACT.get().gained(capability)) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.SACREFICE, 20, suitArchfi < 3 ? 0 : 2, false, false));
            }
            capability.player_demon_suit = suitArchfi < 3 ? 1 : 2;
            capability.syncPlayerVariables(entity);
        } else {
            capability.player_demon_suit = 0;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleEngraveAndSurvivor(Player entity) {
        if (CARelics.HAND_ENGRAVE.get().get(entity) > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.ENGRAVED_TRIUMPH, 20,
                        CARelics.HAND_ENGRAVE.get().get(entity) - 1, false, false));
        }
        if (CARelics.SURVIVOR_CONTRACT.get().get(entity) > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.SURVIVORS_GUIDE, 20,
                        CARelics.SURVIVOR_CONTRACT.get().get(entity) - 1, false, false));
        }
    }

    private static void handlePlayerLives(Player entity) {
        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
        double playerLives = capability.player_lives;
        double maxLives = capability.player_maxlive;

        if (playerLives > maxLives) {
            capability.player_lives = capability.player_maxlive;
            capability.syncPlayerVariables(entity);
        } else if (playerLives < 1) {
            capability.player_lives = 1;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleSanityDefendEnchant(Player entity) {
        double enchant = 0;
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.FEET)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.FEET));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.LEGS)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.LEGS));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.CHEST)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.CHEST));
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.HEAD)) != 0) {
            enchant = enchant + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_DEFEND), entity.getItemBySlot(EquipmentSlot.HEAD));
        }
        if (enchant > 16) enchant = 16;
        if (enchant > 0 && !entity.hasEffect(CAMobEffects.SANIDY_DEFENDER) && !entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CAMobEffects.SANIDY_DEFENDER, 20, (int) (enchant - 1), false, false));
        }
    }

    private static void handleOceanizationEffects(Player entity) {
        if (ModCapabilities.getPlayerVariables(entity).player_oceanization >= 3) {
            if (entity.isUnderWater() && !entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0));
            }
        }
    }

    private static void handleRelicHemost(Player entity) {
        if (CARelics.HEMOST.get().gained(entity)) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.HEMOSTATIC, 20, 0, false, false));
        }
    }

    private static void handleRelicYearning(Player entity) {
        if (CARelics.YEARNING.get().gained(entity)) {
            if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemStack.EMPTY.getItem())) {
                double amplifi = Math.min(Math.floor(entity.experienceLevel * 0.25), 64);
                if (amplifi >= 1 && !entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.UNRIPE_THOUGHTS, 20, (int) amplifi, false, false));
                }
            }
        }
    }
}