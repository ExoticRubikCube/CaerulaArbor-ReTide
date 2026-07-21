package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.NodeUtils;
import com.apocalypse.caerulaarbor.util.PlayerStateUtils;
import com.apocalypse.caerulaarbor.util.RelicUtils;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class PlayerTickEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            handleArmorEnchantFunc(event.player);
            handleBanRelicFunc(event.player);
            handleEssenceResistanceWithIce(event.player);
            handleHandSwipeFunc(event.player);
            handleNetherseaWalkerExtraFunc(event.player);
            handlePlayerEvolutionTick(event.player);
            handlePlayerTickFunc(event.player);
        }
    }

    private static void handleArmorEnchantFunc(Player entity) {
        if (entity == null) return;

        ItemStack helm = (entity.getItemBySlot(EquipmentSlot.HEAD)).copy();
        ItemStack chest = (entity.getItemBySlot(EquipmentSlot.CHEST)).copy();
        ItemStack legg = (entity.getItemBySlot(EquipmentSlot.LEGS)).copy();
        ItemStack boot = (entity.getItemBySlot(EquipmentSlot.FEET)).copy();

        if (entity.tickCount % 5 == 0) {
            double lvl = helm.getEnchantmentLevel(CAEnchantments.FLEXIBILITY.get()) + chest.getEnchantmentLevel(CAEnchantments.FLEXIBILITY.get())
                    + legg.getEnchantmentLevel(CAEnchantments.FLEXIBILITY.get()) + boot.getEnchantmentLevel(CAEnchantments.FLEXIBILITY.get());
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.FLEXIBILITY_BUFF.get(), 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = helm.getEnchantmentLevel(CAEnchantments.MAGIC_TOLERANCE.get()) + chest.getEnchantmentLevel(CAEnchantments.MAGIC_TOLERANCE.get())
                    + legg.getEnchantmentLevel(CAEnchantments.MAGIC_TOLERANCE.get()) + boot.getEnchantmentLevel(CAEnchantments.MAGIC_TOLERANCE.get());
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.MAGIC_RESIS_BUFF.get(), 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = helm.getEnchantmentLevel(CAEnchantments.SANITY_INJURY_CURSE.get()) + chest.getEnchantmentLevel(CAEnchantments.SANITY_INJURY_CURSE.get())
                    + legg.getEnchantmentLevel(CAEnchantments.SANITY_INJURY_CURSE.get()) + boot.getEnchantmentLevel(CAEnchantments.SANITY_INJURY_CURSE.get());
            if (lvl > 0) {
                SIHelper.causeSanityInjury(entity, lvl);
            }
        }

        double lvl0 = helm.getEnchantmentLevel(CAEnchantments.HAZARD_PROTECTION.get());
        double lvl1 = chest.getEnchantmentLevel(CAEnchantments.HAZARD_PROTECTION.get());
        double lvl2 = legg.getEnchantmentLevel(CAEnchantments.HAZARD_PROTECTION.get());
        double lvl3 = boot.getEnchantmentLevel(CAEnchantments.HAZARD_PROTECTION.get());
        double lvl = lvl0 + lvl1 + lvl2 + lvl3;

        if (lvl > 0) {
            double gap = Math.max(600 - 25 * lvl, 300);
            double maxAmplif = Math.min(Math.max(Math.max(lvl0, lvl1), Math.max(lvl2, lvl3)), 2);
            if (entity.tickCount % gap == 64) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 260, (int) (maxAmplif - 1), false, false));
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
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
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
            });
        }
    }

    private static void handleEssenceResistanceWithIce(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 2 == 0 && entity.hasEffect(CAMobEffects.ESSENCE_RESISTANCE.get())) {
            if (entity.getTicksFrozen() < 140) entity.setTicksFrozen(Math.max(entity.getTicksFrozen() - 1, 0));
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks() - 1, 0));
        }
    }

    private static void handleHandSwipeFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 20 != 0) return;

        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWIPE) {
            if (!((entity.getOffhandItem()).getItem() == net.minecraft.world.item.Items.BRUSH)) return;

            if (entity.hasEffect(MobEffects.REGENERATION)) {
                LevelAccessor world = entity.level();
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();

                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof net.minecraft.world.entity.monster.Monster && entity.distanceTo(entityiterator) < 8) {
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
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.NETHERSEA_WALKER.get(), boots) != 0) {
            double lvl = boots.getEnchantmentLevel(CAEnchantments.NETHERSEA_WALKER.get());
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL.get(), 30, (int) lvl, false, false));
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
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.disoclusion = -1;
                    capability.syncPlayerVariables(entity);
                });
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
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY.get(), 20, 0, false, false));
                } else if (addDef == 2) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY.get(), 20, 2, false, false));
                } else if (addDef == 3) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY.get(), 20, 5, false, false));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_PERCLY_TINY.get(), 20, 2, false, false));
                } else if (addDef == 4) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_TINY.get(), 20, 9, false, false));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DEF_PERCLY_TINY.get(), 20, 7, false, false));
                }
            }
        }

        addDef = NodeUtils.getNodeAddResis(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_RESIS_TINY.get(), 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddSpeed(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddSanity(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 9, false, false));
        }

        addDef = NodeUtils.getNodeAddMiss(entity);
        boolean lowerHealth = EntityUtils.getHealthPerc(entity) <= 0.5;
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE.get(), 20, 2, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE.get(), 20, 7, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE.get(), 20, lowerHealth ? 22 : 14, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_MISS_RATE.get(), 20, lowerHealth ? 41 : 23, false, false));
        }

        addDef = NodeUtils.getNodeEunectes(entity);
        if (addDef > 0 && !lowerHealth && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_DAMAGE_TINY.get(), 20, 9, false, false));
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
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
            modifi = modifi * 0.33;
        }
        if (entity.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
            entity.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(modifi);
    }

    private static void handleKingSuit(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives > 1) return;

        double suitKing = 0;
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_SPEAR) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BOOST.get(), 20, 1, false, false));
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_ARMOR) {
            suitKing = suitKing + 1;
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_EXTENSION) {
            suitKing = suitKing + 1;
            double amplifi = Math.ceil(entity.getMaxHealth() / 20);
            if (amplifi > 24) amplifi = 24;
            if ((entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < amplifi) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (int) amplifi, false, false));
            }
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CROWN) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.KINGS_BREATH.get(), 20, suitKing < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitKing < 3 ? 1 : 2;
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_king_suit = suitLevel;
                capability.syncPlayerVariables(entity);
            });
        } else {
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_king_suit = 0;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleHandSpeed(Player entity, LevelAccessor world, double x, double y, double z) {
        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SPEED) {
            if (mainHandItem.getItem() instanceof PickaxeItem || mainHandItem.is(ItemTags.create(ResourceLocation.parse("minecraft:pickaxes")))) {
                boolean valid = true;
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
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
                    entity.addEffect(new MobEffectInstance(CAMobEffects.HANDS_SPEED.get(), 20, 2));
                }
            }
        }
    }

    private static void handleArchfiSuit(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives < (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive)
            return;

        double suitArchfi = 0;
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_FLAG) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.FLAG_SWINGS.get(), 20, 2, false, false));
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_BED) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.KEEP_BEDDING.get(), 20, 0, false, false));
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_ARTIFACT) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CAMobEffects.SACREFICE.get(), 20, suitArchfi < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitArchfi < 3 ? 1 : 2;
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_demon_suit = suitLevel;
                capability.syncPlayerVariables(entity);
            });
        } else {
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_demon_suit = 0;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleEngraveAndSurvivor(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.ENGRAVED_TRIUMPH.get(), 20,
                        (int) ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE - 1), false, false));
        }
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.SURVIVORS_GUIDE.get(), 20,
                        (int) ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR - 1), false, false));
        }
    }

    private static void handlePlayerLives(Player entity) {
        double playerLives = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives;
        double maxLives = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive;

        if (playerLives > maxLives) {
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_lives = capability.player_maxlive;
                capability.syncPlayerVariables(entity);
            });
        } else if (playerLives < 1) {
            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.player_lives = 1;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleSanityDefendEnchant(Player entity) {
        double enchant = 0;
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.FEET)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.LEGS)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.LEGS).getEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.CHEST)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.HEAD)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(CAEnchantments.SANITY_DEFEND.get());
        }
        if (enchant > 16) enchant = 16;
        if (enchant > 0 && !entity.hasEffect(CAMobEffects.SANIDY_DEFENDER.get()) && !entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CAMobEffects.SANIDY_DEFENDER.get(), 20, (int) (enchant - 1), false, false));
        }
    }

    private static void handleOceanizationEffects(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
            if (entity.isUnderWater() && !entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0));
            }
        }
    }

    private static void handleRelicHemost(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_HEMOST) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.HEMOSTATIC.get(), 20, 0, false, false));
        }
    }

    private static void handleRelicYearning(Player entity) {
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_YEARNING) {
            if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemStack.EMPTY.getItem())) {
                double amplifi = Math.min(Math.floor(entity.experienceLevel * 0.25), 64);
                if (amplifi >= 1 && !entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(CAMobEffects.UNRIPE_THOUGHTS.get(), 20, (int) amplifi, false, false));
                }
            }
        }
    }
}
