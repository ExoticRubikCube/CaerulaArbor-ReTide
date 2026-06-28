package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.system.UpgradeMigraProcedure;
import com.apocalypse.caerulaarbor.system.UpgradeSilenceProcedure;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class LivingTickEventHandler {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() == null) return;

        handleSanityTick(event);
        handleChangeAttackGoal(event);
        handleDefensiveMode(event);
        handleSeabornAggresive(event);
        handleMobTick(event);
    }

    private static void handleSanityTick(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;

        double sanity = entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get())
                ? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue()
                : 0;

        if (sanity < 0) {
            if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
                _livingEntity1.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(0);
            if (entity instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "terror_of_collapsing"));
                AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String criteria : _ap.getRemainingCriteria())
                        _player.getAdvancements().award(_adv, criteria);
                }
            }
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SANITY_IMMUE.get(), 200, 0, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 200, 0, false, false));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0, false, true));
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.UNDER_BREAK.get(), 200, 0, false, true));
            if (!(entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(CaerulaArborModMobEffects.SANITY_IMMUE.get()))) {
                if (entity instanceof LivingEntity _livingEntity8 && _livingEntity8.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
                    _livingEntity8.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(1000);
            }
            double damage = CaerulaConfigsConfiguration.SANITY_BREAK.get();
            if (entity instanceof Player) {
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sanity_break")))), (float) damage);
                if (!world.isClientSide()) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "nervous_break")), SoundSource.AMBIENT, 3, 1);
                    }
                }
            } else {
                if (!world.isClientSide()) {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "nervous_break")), SoundSource.AMBIENT, 2, 1);
                    }
                }
                if (entity instanceof LivingEntity _livingEntity18 && _livingEntity18.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()))
                    _livingEntity18.getAttribute(CaerulaArborModAttributes.NUMB.get()).setBaseValue(Math.max(
                            entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()) ? _livingEntity17.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue() : 0,
                            3));
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sanity_break")))),
                        (float) Math.min(Math.max((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.4, damage), damage * 6));
            }
        }
    }

    private static void handleChangeAttackGoal(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;
        if (entity.tickCount % 30 != 1 || !(entity instanceof LivingEntity)) return;

        Entity enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
        if (enemy == null) return;

        Entity other = null;

        if (enemy instanceof TideDeathrepellerEntity && enemy instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
            other = world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
        } else if (enemy instanceof TideBishopEntity && enemy instanceof LivingEntity _livEnt8 && _livEnt8.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
            other = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
        } else if (enemy instanceof MartusEntity && enemy instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get())) {
            Entity tgt_ent = null;
            Entity tgt_blessed = null;
            double num = 0;
            double max_h = 999;
            double blesses_h = 999;
            double d1 = 0;
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                if (!(entityiterator instanceof Mob)) continue;
                if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) continue;
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
        } else if ((enemy instanceof Endspeaker0Entity || enemy instanceof Endspeaker1Entity || enemy instanceof Endspeaker2Entity)
                && enemy instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get())) {
            double minDist = 999;
            double d = 0;
            Entity enemy1 = null;
            for (Entity entityiterator : world.getEntities(enemy, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                if (!(entityiterator instanceof LivingEntity)) continue;
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    d = enemy.distanceTo(entityiterator);
                    if (d < minDist) {
                        minDist = d;
                        enemy1 = entityiterator;
                    }
                }
            }
            other = enemy1;
        } else if (enemy instanceof OceanizedIllusionerEntity) {
            other = world.getEntitiesOfClass(OceanIllusionEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
        } else if (enemy instanceof OceanizedEnderinaEntity && !EntityPredicateUtils.isEnderinaDurative(entity)) {
            other = world.getEntitiesOfClass(MoistEnderCrystalEntity.class, AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> true).stream()
                    .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(x, y, z))).findFirst().orElse(null);
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
        if (!world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.DEFENSIVE_MODE)) return;

        double minDist = 999;
        Entity enemy = null;
        Entity curEnemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;

        if (curEnemy != null && curEnemy.isAlive()) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sea_friend")))) return;

        for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 12), (z + 32), (x - 32), (y - 9), (z - 32)))) {
            if (entity.isInWater() ^ entityiterator.isInWater()) continue;
            if (!(entityiterator instanceof Monster)) continue;
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) continue;
                double dist = entity.distanceTo(entityiterator);
                if (dist < minDist) {
                    minDist = dist;
                    enemy = entityiterator;
                }
            }
        }

        if (enemy != null) {
            if (entity instanceof Mob _entity && enemy instanceof LivingEntity _ent)
                _entity.setTarget(_ent);
        }
    }

    private static void handleSeabornAggresive(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        Entity entity = event.getEntity();

        if (entity == null) return;
        if (!(entity instanceof Monster)) return;

        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))
                && world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.AGGRESIVE_MODE)) {
            if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(CaerulaArborModMobEffects.ANGER_OF_TIDE.get()))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ANGER_OF_TIDE.get(), 20, 0, false, false));
            }
        }
    }

    private static void handleMobTick(LivingEvent.LivingTickEvent event) {
        LevelAccessor world = event.getEntity().level();
        double x = event.getEntity().getX();
        double y = event.getEntity().getY();
        double z = event.getEntity().getZ();
        Entity entity = event.getEntity();

        if (entity == null) return;
        if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) return;

        handleMobTargeting(world, x, y, z, entity);
        handleMobBuffs(world, x, y, z, entity);
        handleNaturalEvolution(world, x, y, z, entity);
    }

    private static void handleMobTargeting(LevelAccessor world, double x, double y, double z, Entity entity) {
        Entity enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
        if (enemy == null || !enemy.isAlive()) {
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                if (entity instanceof Monster && entity.tickCount % 40 == 5) {
                    final Vec3 _center1 = new Vec3(x, y, z);
                    List<Entity> _entfound1 = world.getEntitiesOfClass(Entity.class, new AABB(_center1, _center1).inflate(48 / 2d), e1 -> true).stream()
                            .sorted(Comparator.comparingDouble(_entcnd1 -> _entcnd1.distanceToSqr(_center1))).toList();
                    for (Entity entityiterator1 : _entfound1) {
                        if (entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunters")))
                                || entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))
                                || entityiterator1 instanceof TheLastKnightEntity
                                || entityiterator1 instanceof LastKnightAndHorseEntity) {
                            if (entity instanceof Mob _entity1 && entityiterator1 instanceof LivingEntity _ent)
                                _entity1.setTarget(_ent);
                        }
                    }
                }
            }
        }
    }

    private static void handleMobBuffs(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting >= 3) {
            if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MobEffects.DAMAGE_RESISTANCE))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9999, (int) (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting - 3)));
            }
        }

        if (entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.POWER_OF_ANCHOR.get())) return;

        if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence > 0) {
            handleSilenceBuffs(world, x, y, z, entity);
        }
    }

    private static void handleSilenceBuffs(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 3) {
            if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get()))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get(), 9999, (int) (CaerulaArborModVariables.MapVariables.get(world).strategy_silence - 1)));
            }

            if (!(entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(CaerulaArborModMobEffects.STRENGTH_OF_CROWD.get()))) {
                double amplifi = -1;
                double range = CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 4 ? 64 : 32;
                double maxAmp = CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 4 ? 29 : 9;
                int ampStep = CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 4 ? 2 : 1;

                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(range / 2d), e -> true).stream()
                        .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator != entity && entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                        amplifi = amplifi + ampStep;
                    }
                    if (amplifi > maxAmp) {
                        amplifi = maxAmp;
                        break;
                    }
                }

                if (amplifi >= 0) {
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.STRENGTH_OF_CROWD.get(), 9999, (int) amplifi, false, false));
                }
            }
        } else {
            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                if (!(entity instanceof LivingEntity _livEnt16 && _livEnt16.hasEffect(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get()))) {
                    if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                        _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get(), 9999, (int) (CaerulaArborModVariables.MapVariables.get(world).strategy_silence - 1)));
                }
            } else {
                if (entity instanceof LivingEntity _entity)
                    _entity.removeEffect(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get());
            }
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(CaerulaArborModMobEffects.STRENGTH_OF_CROWD.get());
        }

        if (!(entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(MobEffects.REGENERATION)) && !(entity instanceof MartusEntity)) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 9999, (int) (CaerulaArborModVariables.MapVariables.get(world).strategy_silence - 1)));
        }

        if (entity instanceof Mob _mobEnt23 && _mobEnt23.isAggressive()) {
            if (!(entity instanceof LivingEntity _livEnt24 && _livEnt24.hasEffect(MobEffects.MOVEMENT_SPEED))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 9999, (int) (CaerulaArborModVariables.MapVariables.get(world).strategy_silence - 1)));
            }
        }
    }

    private static void handleNaturalEvolution(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.NATURAL_EVOLUTION)) return;
        if (entity.tickCount % 10 != 0) return;
        if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) return;

        if (Math.random() < 0.16 && !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e -> true).isEmpty()) {
            double pnt = Mth.nextDouble(RandomSource.create(), 0, 0.005);
            CaerulaArborModVariables.MapVariables.get(world).evo_point_migration = CaerulaArborModVariables.MapVariables.get(world).evo_point_migration + pnt
                    * Math.max(CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting + CaerulaArborModVariables.MapVariables.get(world).strategy_grow + CaerulaArborModVariables.MapVariables.get(world).strategy_breed, 1);
            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
            UpgradeMigraProcedure.execute(world);
            UpgradeSilenceProcedure.execute(world, pnt);
        }
    }
}
