package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.NodeUtils;
import com.apocalypse.caerulaarbor.utils.PlayerStateUtils;
import com.apocalypse.caerulaarbor.utils.RelicUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
            double lvl = helm.getEnchantmentLevel(CaerulaArborModEnchantments.FLEXIBILITY.get()) + chest.getEnchantmentLevel(CaerulaArborModEnchantments.FLEXIBILITY.get())
                    + legg.getEnchantmentLevel(CaerulaArborModEnchantments.FLEXIBILITY.get()) + boot.getEnchantmentLevel(CaerulaArborModEnchantments.FLEXIBILITY.get());
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FLEXIBILITY_BUFF.get(), 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = helm.getEnchantmentLevel(CaerulaArborModEnchantments.MAGIC_TOLERANCE.get()) + chest.getEnchantmentLevel(CaerulaArborModEnchantments.MAGIC_TOLERANCE.get())
                    + legg.getEnchantmentLevel(CaerulaArborModEnchantments.MAGIC_TOLERANCE.get()) + boot.getEnchantmentLevel(CaerulaArborModEnchantments.MAGIC_TOLERANCE.get());
            if (lvl > 0) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.MAGIC_RESIS_BUFF.get(), 10, (int) Math.min(lvl - 1, 16), false, false));
            }

            lvl = helm.getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_INJURY_CURSE.get()) + chest.getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_INJURY_CURSE.get())
                    + legg.getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_INJURY_CURSE.get()) + boot.getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_INJURY_CURSE.get());
            if (lvl > 0) {
                EntityUtils.deductSanity(entity, lvl);
            }
        }

        double lvl0 = helm.getEnchantmentLevel(CaerulaArborModEnchantments.HAZARD_PROTECTION.get());
        double lvl1 = chest.getEnchantmentLevel(CaerulaArborModEnchantments.HAZARD_PROTECTION.get());
        double lvl2 = legg.getEnchantmentLevel(CaerulaArborModEnchantments.HAZARD_PROTECTION.get());
        double lvl3 = boot.getEnchantmentLevel(CaerulaArborModEnchantments.HAZARD_PROTECTION.get());
        double lvl = lvl0 + lvl1 + lvl2 + lvl3;

        if (lvl > 0) {
            double gap = Math.max(600 - 25 * lvl, 300);
            double maxAmplif = Math.min(Math.max(Math.max(lvl0, lvl1), Math.max(lvl2, lvl3)), 2);
            if (entity.tickCount % gap == 64) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ESSENCE_RESISTANCE.get(), 260, (int) (maxAmplif - 1), false, false));
            }
        }
    }

    private static void handleBanRelicFunc(Player entity) {
        if (entity == null) return;
        if (!CaerulaConfigsConfiguration.RELIC_BAN.get()) return;

        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if (mainHandItem.is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_advanced")))) {
            entity.getMainHandItem().setCount(0);
        }
        mainHandItem = (entity.getOffhandItem()).copy();
        if (mainHandItem.is(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_advanced")))) {
            entity.getOffhandItem().setCount(0);
        }

        if (entity.tickCount % 20 == 10) {
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
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
        if (entity.tickCount % 2 == 0 && entity.hasEffect(CaerulaArborModMobEffects.ESSENCE_RESISTANCE.get())) {
            if (entity.getTicksFrozen() < 140) entity.setTicksFrozen(Math.max(entity.getTicksFrozen() - 1, 0));
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks() - 1, 0));
        }
    }

    private static void handleHandSwipeFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 20 != 0) return;

        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_SWIPE) {
            if (!((entity.getOffhandItem()).getItem() == net.minecraft.world.item.Items.BRUSH)) return;

            if (entity.hasEffect(MobEffects.REGENERATION)) {
                LevelAccessor world = entity.level();
                double x = entity.getX();
                double y = entity.getY();
                double z = entity.getZ();

                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof net.minecraft.world.entity.monster.Monster && entity.distanceTo(entityiterator) < 8) {
                        entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "wipe_magic"))), entity),
                                (float) (5 * (1 + (entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0))));
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.WAX_OFF, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 12, 0.8, 1, 0.8, 0.1);
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

        if (!world.getBlockState(BlockPos.containing(x, y - 0.5, z)).is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "nethersea_walker_functions")))) return;

        ItemStack boots = (entity.getItemBySlot(EquipmentSlot.FEET)).copy();
        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.NETHERSEA_WALKER.get(), boots) != 0) {
            double lvl = boots.getEnchantmentLevel(CaerulaArborModEnchantments.NETHERSEA_WALKER.get());
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.RUNNING_ON_TRAIL.get(), 30, (int) lvl, false, false));
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
                entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.disoclusion = -1;
                    capability.syncPlayerVariables(entity);
                });
            }
        }

        if (PlayerStateUtils.isNexusRegSanitySelected(entity)) {
            EntityUtils.restoreSanity(entity, 1);
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
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_TINY.get(), 20, 0, false, false));
                } else if (addDef == 2) {
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_TINY.get(), 20, 2, false, false));
                } else if (addDef == 3) {
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_TINY.get(), 20, 5, false, false));
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_PERCLY_TINY.get(), 20, 2, false, false));
                } else if (addDef == 4) {
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_TINY.get(), 20, 9, false, false));
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DEF_PERCLY_TINY.get(), 20, 7, false, false));
                }
            }
        }

        addDef = EntityUtils.getNodeAddResis(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_RESIS_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_RESIS_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_RESIS_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_RESIS_TINY.get(), 20, 9, false, false));
        }

        addDef = EntityUtils.getNodeAddSpeed(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_SPEED_TINY.get(), 20, 9, false, false));
        }

        addDef = EntityUtils.getNodeAddSanity(entity);
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REDUCE_SANITY_MODIFIER.get(), 20, 9, false, false));
        }

        addDef = EntityUtils.getNodeAddMiss(entity);
        boolean lowerHealth = EntityUtils.getHealthPerc(entity) <= 0.5;
        if (addDef > 0 && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_MISS_RATE.get(), 20, 2, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_MISS_RATE.get(), 20, 7, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_MISS_RATE.get(), 20, lowerHealth ? 22 : 14, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_MISS_RATE.get(), 20, lowerHealth ? 41 : 23, false, false));
        }

        addDef = EntityUtils.getNodeEutectes(entity);
        if (addDef > 0 && !lowerHealth && !entity.level().isClientSide()) {
            if (addDef == 1) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DAMAGE_TINY.get(), 20, 0, false, false));
            else if (addDef == 2) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DAMAGE_TINY.get(), 20, 2, false, false));
            else if (addDef == 3) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DAMAGE_TINY.get(), 20, 5, false, false));
            else if (addDef == 4) entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_DAMAGE_TINY.get(), 20, 9, false, false));
        }
    }

    private static void handlePlayerTickFunc(Player entity) {
        if (entity == null) return;
        if (entity.tickCount % 10 != 0) return;

        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        handleDisoclusionEffects(entity);
        handleSanityModifier(entity);
        handleKingSuit(entity);
        handleHandSpeed(entity, world, x, y, z);
        handleArchfiSuit(entity);
        handleEngraveAndSurvivor(entity);
        handlePlayerLives(entity);
        handleSanityCap(entity);
        handleSanityDefendEnchant(entity);
        handleOceanizationEffects(entity);
        handleRelicHemost(entity);
        handleRelicYearning(entity);
    }

    private static void handleDisoclusionEffects(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).disoclusion == 2) {
            if (!entity.hasEffect(CaerulaArborModMobEffects.HAEMOPHILIA.get())) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.HAEMOPHILIA.get(), 10000, 1, false, false));
            }
        } else {
            entity.removeEffect(CaerulaArborModMobEffects.HAEMOPHILIA.get());
        }

        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).disoclusion == 4) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FLESHDEFORMITY.get(), 999, 1, false, false));
        } else {
            if (entity.hasEffect(CaerulaArborModMobEffects.FLESHDEFORMITY.get())) {
                entity.removeEffect(CaerulaArborModMobEffects.HAEMOPHILIA.get());
            }
        }
    }

    private static void handleSanityModifier(Player entity) {
        double modifi = 1;
        if (PlayerStateUtils.isLightDim(entity)) {
            modifi = 1.2;
        } else if (PlayerStateUtils.isLightCeased(entity)) {
            modifi = 1.5;
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 3) {
            modifi = modifi * 0.33;
        }
        if (entity.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
            entity.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(modifi);
    }

    private static void handleKingSuit(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives > 1) return;

        double suitKing = 0;
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_king_SPEAR) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.KINGS_BOOST.get(), 20, 1, false, false));
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_king_ARMOR) {
            suitKing = suitKing + 1;
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_king_EXTENSION) {
            suitKing = suitKing + 1;
            double amplifi = Math.ceil(entity.getMaxHealth() / 20);
            if (amplifi > 24) amplifi = 24;
            if ((entity.hasEffect(MobEffects.REGENERATION) ? entity.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < amplifi) {
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (int) amplifi, false, false));
            }
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_king_CROWN) {
            suitKing = suitKing + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.KINGS_BREATH.get(), 20, suitKing < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitKing < 3 ? 1 : 2;
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_king_suit = suitLevel;
                capability.syncPlayerVariables(entity);
            });
        } else {
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_king_suit = 0;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleHandSpeed(Player entity, LevelAccessor world, double x, double y, double z) {
        ItemStack mainHandItem = (entity.getMainHandItem()).copy();
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_SPEED) {
            if (mainHandItem.getItem() instanceof PickaxeItem || mainHandItem.is(ItemTags.create(new ResourceLocation("minecraft:pickaxes")))) {
                boolean valid = true;
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator == entity) continue;
                    if (entityiterator instanceof ServerPlayer || entityiterator instanceof Player || entityiterator instanceof Animal) {
                        valid = false;
                    }
                }
                if (valid && !entity.level().isClientSide()) {
                    if ((entity.hasEffect(MobEffects.DIG_SPEED) ? entity.getEffect(MobEffects.DIG_SPEED).getAmplifier() : 0) < 2) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2));
                    }
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.HANDS_SPEED.get(), 20, 2));
                }
            }
        }
    }

    private static void handleArchfiSuit(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives < (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_maxlive)
            return;

        double suitArchfi = 0;
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_archfi_FLAG) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FLAG_SWINGS.get(), 20, 2, false, false));
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_archfi_BED) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.KEEP_BEDDING.get(), 20, 0, false, false));
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_archfi_ARTIFACT) {
            suitArchfi = suitArchfi + 1;
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SACREFICE.get(), 20, suitArchfi < 3 ? 0 : 2, false, false));
            }
            final double suitLevel = suitArchfi < 3 ? 1 : 2;
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_demon_suit = suitLevel;
                capability.syncPlayerVariables(entity);
            });
        } else {
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_demon_suit = 0;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleEngraveAndSurvivor(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_ENGRAVE > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ENGRAVED_TRIUMPH.get(), 20,
                        (int) ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_ENGRAVE - 1), false, false));
        }
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_SURVIVOR > 0) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SURVIVORS_GUIDE.get(), 20,
                        (int) ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_SURVIVOR - 1), false, false));
        }
    }

    private static void handlePlayerLives(Player entity) {
        double playerLives = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives;
        double maxLives = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_maxlive;

        if (playerLives > maxLives) {
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_lives = capability.player_maxlive;
                capability.syncPlayerVariables(entity);
            });
        } else if (playerLives < 1) {
            entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_lives = 1;
                capability.syncPlayerVariables(entity);
            });
        }
    }

    private static void handleSanityCap(Player entity) {
        if ((entity.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? entity.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0) > 1000) {
            if (entity.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
                entity.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(1000);
        }
    }

    private static void handleSanityDefendEnchant(Player entity) {
        double enchant = 0;
        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.FEET)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.LEGS)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.LEGS).getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.CHEST)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get());
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get(), entity.getItemBySlot(EquipmentSlot.HEAD)) != 0) {
            enchant = enchant + entity.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(CaerulaArborModEnchantments.SANITY_DEFEND.get());
        }
        if (enchant > 16) enchant = 16;
        if (enchant > 0 && !entity.hasEffect(CaerulaArborModMobEffects.SANIDY_DEFENDER.get()) && !entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SANIDY_DEFENDER.get(), 20, (int) (enchant - 1), false, false));
        }
    }

    private static void handleOceanizationEffects(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 3) {
            if (entity.isUnderWater() && !entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 20, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0));
            }
        }
    }

    private static void handleRelicHemost(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_HEMOST) {
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.HEMOSTATIC.get(), 20, 0, false, false));
        }
    }

    private static void handleRelicYearning(Player entity) {
        if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_YEARNING) {
            if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemStack.EMPTY.getItem())) {
                double amplifi = Math.min(Math.floor(entity.experienceLevel * 0.25), 64);
                if (amplifi >= 1 && !entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.UNRIPE_THOUGHTS.get(), 20, (int) amplifi, false, false));
                }
            }
        }
    }
}
