package com.susen36.caerulaarbor.event;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.entity.AbsorberLimbEntity;
import com.susen36.caerulaarbor.entity.ai.SeabornAggressiveTargetGoal;
import com.susen36.caerulaarbor.entity.ai.SeabornCounterTargetGoal;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.manager.upgrade.MigrationUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SubsistingUpgradeManager;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static com.susen36.caerulaarbor.init.CAEntityTypeTags.*;

@EventBusSubscriber
public class LivingTickEventHandler {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        handleModeGoals(event);
        handleMobTick(event);
        handleArmorEnchantFunc(event);
    }

    private static void handleModeGoals(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            if (!(entity instanceof Mob mob)) {
                return;
            }
            if (entity.tickCount % 20 != 0) {
                return;
            }
            boolean isSeaborn = entity.getType().is(SEABORN);
            boolean isSeabornPet = entity.getType().is(CAEntityTypeTags.SEABORN_PET);
            boolean isSeaFriend = entity.getType().is(CAEntityTypeTags.SEA_FRIEND);
            if (isSeaborn && !isSeabornPet && entity.level().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE)) {
                boolean alreadyAdded = false;
                for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
                    if (goal.getGoal() instanceof SeabornAggressiveTargetGoal) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    mob.targetSelector.addGoal(8, new SeabornAggressiveTargetGoal(mob));
                }
            }
            if (!isSeaborn && !isSeaFriend && entity.level().getGameRules().getBoolean(CAGameRules.DEFENSIVE_MODE)) {
                boolean alreadyAdded = false;
                for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
                    if (goal.getGoal() instanceof SeabornCounterTargetGoal) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    mob.targetSelector.addGoal(9, new SeabornCounterTargetGoal(mob));
                }
            }
        }
    }

    //TODO有性能问题
    private static void handleMobTick(EntityTickEvent.Post event) {
        Level world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity.getType().is(SEABORN))
        {
            handleMobBuffs(world, entity);
            handleNaturalEvolution(world, x, y, z, entity);
        }
    }

    private static void handleMobBuffs(Level world, Entity entity) {
        double subsistingLevel = MapVariables.get(world).strategy_subsisting;
        int resistLvl = SubsistingUpgradeManager.getSubsistResistLevel(subsistingLevel);
        if (resistLvl >= 0) {
            if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, resistLvl));
            }
        }

        if (entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.POWER_OF_ANCHOR)) return;

        handleSublimationBuffs(world, entity);
    }

    //TODO 可以下放到海嗣基类实现
    private static void handleSublimationBuffs(Level world, Entity entity) {
        if (!world.isClientSide()&&entity.getType().is(SEABORN_BOSS)) {
            double subl = MapVariables.get(world).strategy_sublimation;
            double subs = MapVariables.get(world).strategy_subsisting;
            double migra = MapVariables.get(world).strategy_migration;
            double finalSubs = Math.min(subs, subl);
            double finalMigra = Math.min(migra, subl);

            if (entity instanceof LivingEntity living) {
                double maxHealth = living.getMaxHealth();
                double notHurtTick = entity.tickCount - entity.getPersistentData().getDouble("caerula.lastHurtByTime");

                if (notHurtTick >= 220.0 - 20.0 * finalSubs && living.tickCount % 20 == 0) {
                    if (finalSubs > 0.0 && !(entity instanceof AbsorberLimbEntity)) {
                        living.heal((float) (maxHealth * finalSubs * 0.01 * 0.05));
                    }
                }

                if (!entity.getPersistentData().getBoolean("sublimationBlessed") && finalMigra >= 3.0) {
                    float currentHealth = living.getHealth();
                    if (currentHealth < maxHealth * 0.3) {
                        boolean isElite = entity.getType().is(OCEAN_ELITE);
                        boolean isTiny = entity.getType().is(TINY_SEABORN);
                        entity.getPersistentData().putBoolean("sublimationBlessed", true);

                        if (!isTiny) {
                            if (finalMigra == 3.0) {
                                if (isElite) {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 100, 0, false, false));
                                } else {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 60, 0, false, false));
                                }
                            } else if (finalMigra == 4.0) {
                                if (isElite) {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 200, 0, false, false));
                                } else {
                                    living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL, 100, 0, false, false));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void handleNaturalEvolution(Level world, double x, double y, double z, Entity entity) {
        if (!world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) return;
        if (entity.tickCount % 10 != 0) return;
        if (entity.getType().is(SEABORN_PET)) return;

        if (Math.random() < 0.16 && world.hasNearbyAlivePlayer(x, y, z, 83.0)) {
            double pnt = Mth.nextDouble(RandomSource.create(), 0, 0.005);
            MapVariablesHandler.addEvoPoint(world, StrategyType.MIGRATION,
                    pnt * Math.max(MapVariables.get(world).strategy_subsisting + MapVariables.get(world).strategy_grow + MapVariables.get(world).strategy_breed, 1));
            MigrationUpgradeManager.applyMigrationUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, pnt);
        }
    }

    //TODO 之后需要移除buff依赖
    private static void handleArmorEnchantFunc(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            ItemStack helm = living.getItemBySlot(EquipmentSlot.HEAD);
            ItemStack chest = living.getItemBySlot(EquipmentSlot.CHEST);
            ItemStack legg = living.getItemBySlot(EquipmentSlot.LEGS);
            ItemStack boot = living.getItemBySlot(EquipmentSlot.FEET);
            // 四格全空则不可能携带本类附魔，跳过每 tick 的附魔查询以省开销
            if (!helm.isEmpty() || !chest.isEmpty() || !legg.isEmpty() || !boot.isEmpty()) {
                if (living.tickCount % 5 == 0) {
                    Holder<Enchantment> flexibility = CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.FLEXIBILITY);
                    Holder<Enchantment> sanityInjury = CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.SANITY_INJURY_CURSE);
                    double lvl = EnchantmentHelper.getItemEnchantmentLevel(flexibility, helm) + EnchantmentHelper.getItemEnchantmentLevel(flexibility, chest)
                            + EnchantmentHelper.getItemEnchantmentLevel(flexibility, legg) + EnchantmentHelper.getItemEnchantmentLevel(flexibility, boot);
                    if (lvl > 0) {
                        if (!living.level().isClientSide()) {
                            living.addEffect(new MobEffectInstance(CAMobEffects.FLEXIBILITY_BUFF, 10, (int) Math.min(lvl - 1, 16), false, false));
                        }
                    }
                    lvl = EnchantmentHelper.getItemEnchantmentLevel(sanityInjury, helm) + EnchantmentHelper.getItemEnchantmentLevel(sanityInjury, chest)
                            + EnchantmentHelper.getItemEnchantmentLevel(sanityInjury, legg) + EnchantmentHelper.getItemEnchantmentLevel(sanityInjury, boot);
                    if (lvl > 0) {
                        EPUtils.causeSanityInjury(living, lvl / 20.0);
                    }
                }
                Holder<Enchantment> hazardProtection = CAEnchantments.getHolder(living.level().registryAccess(), CAEnchantments.HAZARD_PROTECTION);
                double lvl0 = EnchantmentHelper.getItemEnchantmentLevel(hazardProtection, helm);
                double lvl1 = EnchantmentHelper.getItemEnchantmentLevel(hazardProtection, chest);
                double lvl2 = EnchantmentHelper.getItemEnchantmentLevel(hazardProtection, legg);
                double lvl3 = EnchantmentHelper.getItemEnchantmentLevel(hazardProtection, boot);
                double lvl = lvl0 + lvl1 + lvl2 + lvl3;
                if (lvl > 0) {
                    double gap = Math.max(600 - 25 * lvl, 300);
                    double maxAmplif = Math.min(Math.max(Math.max(lvl0, lvl1), Math.max(lvl2, lvl3)), 2);
                    if (living.tickCount % (int) gap == 64) {
                        if (!living.level().isClientSide()) {
                            living.addEffect(new MobEffectInstance(BabelMobEffects.ESSENCE_RESISTANCE, 260, (int) (maxAmplif - 1), false, false));
                        }
                    }
                }
            }
        }
    }
}