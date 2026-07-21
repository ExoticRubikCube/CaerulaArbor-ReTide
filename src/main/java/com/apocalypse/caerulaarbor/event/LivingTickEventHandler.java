package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.manager.MigrationUpgradeManager;
import com.apocalypse.caerulaarbor.manager.SilenceUpgradeManager;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingTickEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() == null) return;

        handleChangeAttackGoal(event);
        handleDefensiveMode(event);
        handleSeabornAggresive(event);
        handleMobTick(event);
    }

    //TODO 可能需要下放
    private static void handleChangeAttackGoal(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null || entity.tickCount % 30 != 1) return;

        Entity enemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (enemy == null) return;

        Entity other = null;

        if (enemy instanceof TideDeathrepellerEntity livEnt5 && livEnt5.hasEffect(CAMobEffects.FAKE_DEATH.get())) {
            other = world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
        } else if (enemy instanceof TideBishopEntity livEnt8 && livEnt8.hasEffect(CAMobEffects.FAKE_DEATH.get())) {
            other = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z))).orElse(null);
        } else if (enemy instanceof MartusEntity livEnt11 && livEnt11.hasEffect(CAMobEffects.INVULNERABLE.get())) {
            Entity tgt_ent = null;
            Entity tgt_blessed = null;
            double max_h = 999;
            double blesses_h = 999;
            double d1;
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                if (!(entityiterator instanceof Mob)) continue;
                if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))))
                    continue;
                if (entityiterator instanceof MartusEntity) continue;
                d1 = entity.distanceTo(entityiterator);
                if (entityiterator.getPersistentData().getBoolean("blessed") && d1 < blesses_h) {
                    blesses_h = d1;
                    tgt_blessed = entityiterator;
                } else if (d1 < max_h) {
                    max_h = d1;
                    tgt_ent = entityiterator;
                }
            }
            other = tgt_blessed != null ? tgt_blessed : tgt_ent;
        } else {
            if (enemy instanceof EndspeakerEntity endspeaker && endspeaker.getPhase() < 3 && endspeaker.hasEffect(CAMobEffects.INVULNERABLE.get())) {
                double minDist = 999;
                double d;
                Entity enemy1 = null;
                for (Entity entityiterator : world.getEntities(enemy, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                    if (!(entityiterator instanceof LivingEntity)) continue;
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        d = enemy.distanceTo(entityiterator);
                        if (d < minDist) {
                            minDist = d;
                            enemy1 = entityiterator;
                        }
                    }
                }
                other = enemy1;
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

    private static void handleDefensiveMode(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;
        if (entity.tickCount % 30 != 15) return;
        if (!world.getLevelData().getGameRules().getBoolean(CAGameRules.DEFENSIVE_MODE)) return;

        double minDist = 999;
        Entity enemy = null;
        Entity curEnemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;

        if (curEnemy != null && curEnemy.isAlive()) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))))
            return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "sea_friend"))))
            return;

        for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 12), (z + 32), (x - 32), (y - 9), (z - 32)))) {
            if (entity.isInWater() ^ entityiterator.isInWater()) continue;
            if (!(entityiterator instanceof Monster)) continue;
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet"))))
                    continue;
                double dist = entity.distanceTo(entityiterator);
                if (dist < minDist) {
                    minDist = dist;
                    enemy = entityiterator;
                }
            }
        }

        if (entity instanceof Mob _entity && enemy instanceof LivingEntity _ent)
            _entity.setTarget(_ent);
    }

    private static void handleSeabornAggresive(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();

        if (entity instanceof Monster && entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))
                && world.getLevelData().getGameRules().getBoolean(CAGameRules.AGGRESIVE_MODE)) {
            if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(CAMobEffects.ANGER_OF_TIDE.get()))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.ANGER_OF_TIDE.get(), 20, 0, false, false));
            }
        }
    }

    //TODO有性能问题
    private static void handleMobTick(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity != null && entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))))
        {
            handleMobTargeting(world, x, y, z, entity);
            handleMobBuffs(world, x, y, z, entity);
            handleNaturalEvolution(world, x, y, z, entity);
        }
    }

    private static void handleMobTargeting(LevelAccessor world, double x, double y, double z, Entity entity) {
        Entity enemy = entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (enemy == null || !enemy.isAlive()) {
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")))) {
                if (entity instanceof Monster && entity.tickCount % 40 == 5) {
                    final Vec3 center1 = new Vec3(x, y, z);
                    List<Entity> entfound1 = world.getEntitiesOfClass(Entity.class, new AABB(center1, center1).inflate(48 / 2d), e1 -> true).stream()
                            .sorted(Comparator.comparingDouble(entcnd1 -> entcnd1.distanceToSqr(center1))).toList();
                    for (Entity entityiterator1 : entfound1) {
                        if (entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "hunters")))
                                || entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "inquisition")))
                                || entityiterator1 instanceof TheLastKnightEntity
                                || entityiterator1 instanceof LastKnightAndHorseEntity) {
                            if (entity instanceof Mob entity1 && entityiterator1 instanceof LivingEntity ent)
                                entity1.setTarget(ent);
                        }
                    }
                }
            }
        }
    }

    private static void handleMobBuffs(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (MapVariables.get(world).strategy_subsisting >= 3) {
            if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9999, (int) (MapVariables.get(world).strategy_subsisting - 3)));
            }
        }

        if (entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.POWER_OF_ANCHOR.get())) return;

        if (MapVariables.get(world).strategy_silence > 0) {
            handleSilenceBuffs(world, x, y, z, entity);
        }

        handleSublimationBuffs(world, entity);
    }

    private static void handleSilenceBuffs(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (MapVariables.get(world).strategy_silence >= 3) {
            if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(CAMobEffects.BOOST_OF_SILENCE.get()))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.BOOST_OF_SILENCE.get(), 9999, (int) (MapVariables.get(world).strategy_silence - 1)));
            }

            if (!(entity instanceof LivingEntity livEnt6 && livEnt6.hasEffect(CAMobEffects.STRENGTH_OF_CROWD.get()))) {
                double amplifi = -1;
                double range = MapVariables.get(world).strategy_silence >= 4 ? 64 : 32;
                double maxAmp = MapVariables.get(world).strategy_silence >= 4 ? 29 : 9;
                int ampStep = MapVariables.get(world).strategy_silence >= 4 ? 2 : 1;

                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(range / 2d), e -> true).stream()
                        .sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator != entity && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        amplifi = amplifi + ampStep;
                    }
                    if (amplifi > maxAmp) {
                        amplifi = maxAmp;
                        break;
                    }
                }

                if (amplifi >= 0) {
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.STRENGTH_OF_CROWD.get(), 9999, (int) amplifi, false, false));
                }
            }
        } else {
            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                if (!(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(CAMobEffects.BOOST_OF_SILENCE.get()))) {
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CAMobEffects.BOOST_OF_SILENCE.get(), 9999, (int) (MapVariables.get(world).strategy_silence - 1)));
                }
            } else {
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(CAMobEffects.BOOST_OF_SILENCE.get());
            }
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(CAMobEffects.STRENGTH_OF_CROWD.get());
        }

        if (!(entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(MobEffects.REGENERATION)) && !(entity instanceof MartusEntity)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 9999, (int) (MapVariables.get(world).strategy_silence - 1)));
        }

        if (entity instanceof Mob _mobEnt23 && _mobEnt23.isAggressive()) {
            if (!_mobEnt23.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9999, (int) (MapVariables.get(world).strategy_silence - 1)));
            }
        }
    }

    private static void handleSublimationBuffs(LevelAccessor world, Entity entity) {
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "bossoffspring")))) {
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
                    boolean isElite = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanelite")));
                    boolean isTiny = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "tiny_seaborn")));
                    entity.getPersistentData().putBoolean("sublimationBlessed", true);

                    if (!isTiny) {
                        if (finalMigra == 3.0) {
                            if (isElite) {
                                living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 100, 0, false, false));
                            } else {
                                living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 60, 0, false, false));
                            }
                        } else if (finalMigra == 4.0) {
                            if (isElite) {
                                living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 200, 0, false, false));
                            } else {
                                living.addEffect(new MobEffectInstance(CAMobEffects.IMMORTAL.get(), 100, 0, false, false));
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
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet")))) return;

        if (Math.random() < 0.16 && !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).isEmpty()) {
            double pnt = Mth.nextDouble(RandomSource.create(), 0, 0.005);
            MapVariablesHandler.addEvoPoint(world, StrategyType.MIGRATION,
                    pnt * Math.max(MapVariables.get(world).strategy_subsisting + MapVariables.get(world).strategy_grow + MapVariables.get(world).strategy_breed, 1));
            MigrationUpgradeManager.applyMigrationUpgrade(world);
            SilenceUpgradeManager.applySilenceUpgrade(world, pnt);
        }
    }
}
