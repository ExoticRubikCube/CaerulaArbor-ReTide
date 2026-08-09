package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.entity.*;
import com.susen36.caerulaarbor.entity.ai.SeabornAggressiveTargetGoal;
import com.susen36.caerulaarbor.entity.ai.SeabornCounterTargetGoal;
import com.susen36.caerulaarbor.entity.enderdragon.MoistEnderCrystalEntity;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedBishopEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedImmortalEntity;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.manager.upgrade.MigrationUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SubsistingUpgradeManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Comparator;

@EventBusSubscriber
public class LivingTickEventHandler {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        handleModeGoals(event);
        handleChangeAttackGoal(event);
        handleMobTick(event);
    }

    private static void handleModeGoals(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (entity.tickCount % 20 != 0) {
            return;
        }
        boolean isSeaborn = entity.getType().is(EntityUtils.SEA_BORN);
        boolean isSeabornPet = entity.getType().is(EntityUtils.SEA_BORN_PET);
        boolean isSeaFriend = entity.getType().is(EntityUtils.SEA_FRIEND);
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

    //TODO 可能需要下放
    private static void handleChangeAttackGoal(EntityTickEvent.Post event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity.tickCount % 30 != 1) return;

        Entity enemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (enemy == null) return;

        Entity other = null;

        if (enemy instanceof TidelinkedImmortalEntity livEnt5 && livEnt5.hasEffect(CAMobEffects.FAKE_DEATH)) {
            other = world.getEntitiesOfClass(TidelinkedBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
        } else if (enemy instanceof TidelinkedBishopEntity livEnt8 && livEnt8.hasEffect(CAMobEffects.FAKE_DEATH)) {
            other = world.getEntitiesOfClass(TidelinkedImmortalEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
        } else if (enemy instanceof MartusEntity livEnt11 && livEnt11.hasEffect(CAMobEffects.INVULNERABLE)) {
            Entity tgt_ent = null;
            Entity tgt_blessed = null;
            double max_h = -1.0D;
            double blesses_h = -1.0D;
            double d1;
            for (Mob entityiterator : world.getEntitiesOfClass(Mob.class, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"))))
                    continue;
                if (entityiterator instanceof MartusEntity) continue;
                d1 = entity.distanceToSqr(entityiterator);
                if (entityiterator.getPersistentData().getBoolean("blessed") && (blesses_h == -1.0D || d1 < blesses_h)) {
                    blesses_h = d1;
                    tgt_blessed = entityiterator;
                } else if (max_h == -1.0D || d1 < max_h) {
                    max_h = d1;
                    tgt_ent = entityiterator;
                }
            }
            other = tgt_blessed != null ? tgt_blessed : tgt_ent;
        } else {
            if (enemy instanceof EndspeakerEntity endspeaker && endspeaker.getPhase() < 3 && endspeaker.hasEffect(CAMobEffects.INVULNERABLE)) {
                other = world.getEntitiesOfClass(LivingEntity.class, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)),
                        e -> e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))
                ).stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(enemy))).orElse(null);
            } else if (enemy instanceof OceanizedIllusionerEntity) {
                other = world.getEntitiesOfClass(OceanIllusionEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
            } else if (enemy instanceof OceanizedEnderinaEntity enderina && !enderina.isEnderinaDurative()) {
                other = world.getEntitiesOfClass(MoistEnderCrystalEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
            }
        }

        if (other != null) {
            if (entity instanceof Mob _entity && other instanceof LivingEntity _ent)
                _entity.setTarget(_ent);
        }
    }

    //TODO有性能问题
    private static void handleMobTick(EntityTickEvent.Post event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"))))
        {
            handleMobBuffs(world, x, y, z, entity);
            handleNaturalEvolution(world, x, y, z, entity);
        }
    }

    private static void handleMobBuffs(LevelAccessor world, double x, double y, double z, Entity entity) {
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

    private static void handleSublimationBuffs(LevelAccessor world, Entity entity) {
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_boss")))) {
            return;
        }
        if (world.isClientSide()) return;

        double subl = MapVariables.get(world).strategy_sublimation;
        double subs = MapVariables.get(world).strategy_subsisting;
        double migra = MapVariables.get(world).strategy_migration;
        double finalSubs = Math.min(subs, subl);
        double finalMigra = Math.min(migra, subl);

        if (entity instanceof LivingEntity living) {
            double maxHealth = living.getMaxHealth();
            double notHurtTick = entity.tickCount - entity.getPersistentData().getDouble("caerula.lastHurtByTime");

            if (notHurtTick >= 220.0 - 20.0 * finalSubs) {
                if (finalSubs > 0.0 && !(entity instanceof AbsorberLimbEntity)) {
                    living.heal((float) (maxHealth * finalSubs * 0.01 * 0.05));
                }
            }

            if (!entity.getPersistentData().getBoolean("sublimationBlessed") && finalMigra >= 3.0) {
                float currentHealth = living.getHealth();
                if (currentHealth < maxHealth * 0.3) {
                    boolean isElite = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "oceanelite")));
                    boolean isTiny = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "tiny_seaborn")));
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

    private static void handleNaturalEvolution(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!world.getLevelData().getGameRules().getBoolean(CAGameRules.NATURAL_EVOLUTION)) return;
        if (entity.tickCount % 10 != 0) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_pet")))) return;

        if (Math.random() < 0.16 && !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).isEmpty()) {
            double pnt = Mth.nextDouble(RandomSource.create(), 0, 0.005);
            MapVariablesHandler.addEvoPoint(world, StrategyType.MIGRATION,
                    pnt * Math.max(MapVariables.get(world).strategy_subsisting + MapVariables.get(world).strategy_grow + MapVariables.get(world).strategy_breed, 1));
            MigrationUpgradeManager.applyMigrationUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, pnt);
        }
    }
}