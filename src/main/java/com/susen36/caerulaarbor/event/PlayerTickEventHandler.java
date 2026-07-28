package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.NodeUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import com.susen36.caerulaarbor.util.RelicUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
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
        handleArmorEnchantFunc(player);
        handleBanRelicFunc(player);
        handleEssenceResistanceWithIce(player);
        handleHandSwipeFunc(player);
        handleNetherseaWalkerExtraFunc(player);
        handlePlayerEvolutionTick(player);
        handlePlayerTickFunc(player);
    }

    private static void handleArmorEnchantFunc(Player entity) {
        if (entity == null) return;

        ItemStack helm = (entity.getItemBySlot(EquipmentSlot.HEAD)).copy();
        ItemStack chest = (entity.getItemBySlot(EquipmentSlot.CHEST)).copy();
        ItemStack legg = (entity.getItemBySlot(EquipmentSlot.LEGS)).copy();
        ItemStack boot = (entity.getItemBySlot(EquipmentSlot.FEET)).copy();

        if (entity.tickCount % 5 == 0) {
            double lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.FLEXIBILITY), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.FLEXIBILITY), chest)
                    + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.FLEXIBILITY), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.FLEXIBILITY), boot);
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.FLEXIBILITY_BUFF, 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), chest)
                    + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.MAGIC_TOLERANCE), boot);
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.MAGIC_RESIS_BUFF, 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), helm) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), chest)
                    + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), legg) + EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE), boot);
            if (lvl > 0) {
                SIHelper.causeSanityInjury(entity, lvl);
            }
        }

        double lvl0 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), helm);
        double lvl1 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), chest);
        double lvl2 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), legg);
        double lvl3 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION), boot);
        double lvl = lvl0 + lvl1 + lvl2 + lvl3;

        if (lvl > 0) {
            double gap = Math.max(600 - 25 * lvl, 300);
            double maxAmplif = Math.min(Math.max(Math.max(lvl0, lvl1), Math.max(lvl2, lvl3)), 2);
            if (entity.tickCount % gap == 64) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE, 260, (int) (maxAmplif - 1), false, false));
            }
        }
    }

    private static void handleBanRelicFunc(Player entity) {
        if (entity == null) return;
        if (!CAConfigs.RELIC_BAN.get()) return;

        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if (mainHandItem.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "relic_advanced")))) {
            entity.getMainHandItem().setCount(0);
        }
        mainHandItem = (entity.getOffhandItem()).copy();
        if (mainHandItem.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "relic_advanced")))) {
            entity.getOffhandItem().setCount(0);
        }

        if (entity.tickCount % 20 == 10) {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.relic_hand_ENGRAVE = -1;
            capability.relic_SURVIVOR = -1;
            capability.relic_king_CROWN = false;
            capability.relic_king_ARMOR = false;
            capability.relic_king_SPEAR = false;
            capability.relic_king_EXTENSION = false;
            capability.relic_king_CRYSTAL = false;
            capability.relic_archfi_FLAG = false;
            capability.relic_archfi_BED = false;
            capability.relic_archifi_RYLFATE = false;
            capability.relic_archfi_ARTIFACT = false;
            capability.relic_hand_THORNS = false;
            capability.relic_hand_STRANGLE = false;
            capability.relic_hand_FERTILITY = false;
            capability.relic_hand_SPEED = false;
            capability.relic_hand_BARREN = false;
            capability.relic_hand_SWIPE = false;
            capability.relic_hand_FIREWORK = false;
            capability.relic_hand_SWORD = false;
            capability.relic_legend_CHITIN = false;
            capability.relic_YEARNING = false;
            capability.relic_TREATY = false;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleEssenceResistanceWithIce(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 2 == 0 && entity.hasEffect(CAMobEffects.ESSENCE_RESISTANCE)) {
            if (entity.getTicksFrozen() < 140) entity.setTicksFrozen(Math.max(entity.getTicksFrozen() - 1, 0));
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks() - 1, 0));
        }
    }

    private static void handleHandSwipeFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 20 != 0) return;

        if (ModCapabilities.getPlayerVariables(entity).relic_hand_SWIPE) {
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

    private static void handleNetherseaWalkerExtraFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 5 != 0) return;

        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        if (!world.getBlockState(BlockPos.containing(x, y - 0.5, z)).is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "nethersea_walker_functions")))) return;

        ItemStack boots = (entity.getItemBySlot(EquipmentSlot.FEET)).copy();
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.NETHERSEA_WALKER), boots) != 0) {
            double lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.NETHERSEA_WALKER), boots);
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL, 30, (int) lvl, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, 0, false, false));
            }
        }
    }

    private static void handlePlayerEvolutionTick(Player entity) {
        if (entity == null) return;
        if (!EntityUtils.canPlayerEvo(entity)) return;

        double tickCount = entity.tickCount;

        if (PlayerStateUtils.isNexusNoRejectionSelected(entity)) {
            if (RelicUtils.hasDiso(entity)) {
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                capability.disoclusion = -1;
                capability.syncPlayerVariables(entity);
            }
        }

        if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
            ModCapabilities.getSanityInjury(entity).heal(1);
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
        if (entity.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER))
            entity.getAttribute(CAAttributes.SANITY_MODIFIER).setBaseValue(modifi);
    }

    private static void handleKingSuit(Player entity) {
        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
        if (capability.player_lives > 1) return;

        double suitKing = 0;
        if (capability.relic_king_SPEAR) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BOOST, 20, 1, false, false));
        }
        if (capability.relic_king_ARMOR) {
            suitKing = suitKing + 1;
        }
        if (capability.relic_king_EXTENSION) {
            suitKing = suitKing + 1;
            double amplifi = Math.ceil(entity.getMaxHealth() / 20);
            if (amplifi > 24) amplifi = 24;
            if ((entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < amplifi) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (int) amplifi, false, false));
            }
        }
        if (capability.relic_king_CROWN) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BREATH, 20, suitKing < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitKing < 3 ? 1 : 2;
            capability.player_king_suit = suitLevel;
            capability.syncPlayerVariables(entity);
        } else {
            capability.player_king_suit = 0;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleHandSpeed(Player entity, LevelAccessor world, double x, double y, double z) {
        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if (ModCapabilities.getPlayerVariables(entity).relic_hand_SPEED) {
            if (mainHandItem.getItem() instanceof PickaxeItem || mainHandItem.is(ItemTags.create(ResourceLocation.parse("minecraft:pickaxes")))) {
                boolean valid = true;
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntities(entity, new AABB(center, center).inflate(8 / 2d));
                for (Entity entityiterator : entfound) {
                    if (entityiterator == entity) continue;
                    if (entityiterator instanceof ServerPlayer || entityiterator instanceof Player || entityiterator instanceof Animal) {
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
        if (capability.relic_archfi_FLAG) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.FLAG_SWINGS, 20, 2, false, false));
        }
        if (capability.relic_archfi_BED) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KEEP_BEDDING, 20, 0, false, false));
        }
        if (capability.relic_archfi_ARTIFACT) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.SACREFICE, 20, suitArchfi < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitArchfi < 3 ? 1 : 2;
            capability.player_demon_suit = suitLevel;
            capability.syncPlayerVariables(entity);
        } else {
            capability.player_demon_suit = 0;
            capability.syncPlayerVariables(entity);
        }
    }

    private static void handleEngraveAndSurvivor(Player entity) {
        if (ModCapabilities.getPlayerVariables(entity).relic_hand_ENGRAVE > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.ENGRAVED_TRIUMPH, 20,
                        (int) (ModCapabilities.getPlayerVariables(entity).relic_hand_ENGRAVE - 1), false, false));
        }
        if (ModCapabilities.getPlayerVariables(entity).relic_SURVIVOR > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.SURVIVORS_GUIDE, 20,
                        (int) (ModCapabilities.getPlayerVariables(entity).relic_SURVIVOR - 1), false, false));
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
        if (ModCapabilities.getPlayerVariables(entity).relic_HEMOST) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.HEMOSTATIC, 20, 0, false, false));
        }
    }

    private static void handleRelicYearning(Player entity) {
        if (ModCapabilities.getPlayerVariables(entity).relic_YEARNING) {
            if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemStack.EMPTY.getItem())) {
                double amplifi = Math.min(Math.floor(entity.experienceLevel * 0.25), 64);
                if (amplifi >= 1 && !entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.UNRIPE_THOUGHTS, 20, (int) amplifi, false, false));
                }
            }
        }
    }
}