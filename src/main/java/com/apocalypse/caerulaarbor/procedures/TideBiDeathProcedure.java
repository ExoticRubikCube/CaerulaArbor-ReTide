package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TellerShotEntity;
import com.apocalypse.caerulaarbor.entity.TideBishopEntity;
import com.apocalypse.caerulaarbor.entity.TideDeathrepellerEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class TideBiDeathProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean keepup;
		Entity nearest = null;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
			keepup = true;
			if (entity instanceof TideDeathrepellerEntity) {
				nearest = world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null);
			}
			if (entity instanceof TideBishopEntity) {
				keepup = true;
				nearest = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null);
			}
			if (nearest == null) {
				keepup = false;
			} else {
                LivingEntity _livEnt6 = (LivingEntity) nearest;
                if (_livEnt6.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
                    keepup = false;
                }
            }
			if (!keepup) {
				if (entity instanceof TideDeathrepellerEntity) {
					((TideDeathrepellerEntity) entity).setAnimation("animation.deathrepeller.die");
				}
				if (entity instanceof TideBishopEntity) {
					((TideBishopEntity) entity).setAnimation("animation.tidebishop.die");
				}
				if (entity instanceof LivingEntity _entity)
					_entity.removeAllEffects();
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), 114514);
			}
		} else {
			if (entity instanceof TideDeathrepellerEntity) {
                Entity enemy = null;
                double num = 0;
                double sklp = 0;
                double dura = 0;
                if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get()))) {
                    sklp = entity instanceof TideDeathrepellerEntity _datEntI ? _datEntI.getEntityData().get(TideDeathrepellerEntity.DATA_skillp) : 0;
                    dura = entity instanceof TideDeathrepellerEntity _datEntI ? _datEntI.getEntityData().get(TideDeathrepellerEntity.DATA_duration) : 0;
                    if (dura > 0) {
                        if (entity instanceof TideDeathrepellerEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(TideDeathrepellerEntity.DATA_duration, (int) (dura - 1));
                    }
                    if (sklp <= 0) {
                        num = 0;
                        {
                            final Vec3 _center = new Vec3(x, y, z);
                            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e1 -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                            for (Entity entityiterator : _entfound) {
                                if (!(entityiterator == entity) && (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) >= 10) {
                                    num = num + 1;
                                }
                            }
                        }
                        if (num >= 2 || (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                            enemy = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
                            if (!(enemy == null) && (enemy != null ? entity.distanceTo(enemy) : -1) <= 4) {
                                entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY()), (enemy.getZ())));
                                if (entity instanceof TideDeathrepellerEntity) {
                                    ((TideDeathrepellerEntity) entity).setAnimation("empty");
                                }
                                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 50, 0, false, false));
                                if (entity instanceof TideDeathrepellerEntity) {
                                    ((TideDeathrepellerEntity) entity).setAnimation("animation.deathrepeller.combo");
                                }
                                CaerulaArborMod.queueServerWork(17, () -> {
                                    if (entity.isAlive()) {
                                        EntityUtils.repellerChop(world, x, y, z, entity, 2);
                                    }
                                });
                                CaerulaArborMod.queueServerWork(23, () -> {
                                    if (entity.isAlive()) {
                                        EntityUtils.repellerChop(world, x, y, z, entity, 2);
                                    }
                                });
                                CaerulaArborMod.queueServerWork(35, () -> {
                                    if (entity.isAlive()) {
                                        EntityUtils.repellerChop(world, x, y, z, entity, 2);
                                    }
                                });
                                CaerulaArborMod.queueServerWork(42, () -> {
                                    if (entity.isAlive()) {
                                        EntityUtils.repellerChop(world, x, y, z, entity, 3.5);
                                    }
                                });
                                if (entity instanceof TideDeathrepellerEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(TideDeathrepellerEntity.DATA_duration, 53);
                                if (entity instanceof TideDeathrepellerEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(TideDeathrepellerEntity.DATA_skillp, 300);
                            }
                        }
                    } else {
                        if (entity instanceof TideDeathrepellerEntity _datEntSetI)
                            _datEntSetI.getEntityData().set(TideDeathrepellerEntity.DATA_skillp, (int) (sklp - 1));
                        if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 3) {
                            if (entity instanceof TideDeathrepellerEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(TideDeathrepellerEntity.DATA_skillp, (int) (sklp - 2));
                        }
                    }
                }
                nearest = world.getEntitiesOfClass(TideBishopEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null);
				if (!(nearest == null) && nearest instanceof LivingEntity _livEnt15 && _livEnt15.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
					EntityUtils.spawnLinkParticles(world, entity, nearest);
					if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 3) {
						if (entity instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
							_livingEntity16.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(40);
					} else if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting >= 4) {
						if (entity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
							_livingEntity17.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(20);
					}
				}
			}
			if (entity instanceof TideBishopEntity) {
                if (entity != null) {
                    double sklp = 0;
                    if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get()))) {
                        sklp = entity instanceof TideBishopEntity _datEntI ? _datEntI.getEntityData().get(TideBishopEntity.DATA_skillp) : 0;
                        if (sklp <= 0) {
                            if (!((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == null)) {
                                if (entity instanceof TideBishopEntity) {
                                    ((TideBishopEntity) entity).setAnimation("animation.tidebishop.cast");
                                }
                                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INVULNERABLE.get(), 50, 0, false, false));
                                CaerulaArborMod.queueServerWork(33, () -> {
                                    if (entity == null)
                                        return;
                                    double sklp1 = 0;
                                    Entity rep = null;
                                    if (entity.isAlive() && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
                                        {
                                            Entity _shootFrom = entity;
                                            Level projectileLevel = _shootFrom.level();
                                            if (!projectileLevel.isClientSide()) {
                                                Projectile _entityToSpawn = new Object() {
                                                    public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
                                                        AbstractArrow entityToSpawn = new TellerShotEntity(CaerulaArborModEntities.TELLER_SHOT.get(), level);
                                                        entityToSpawn.setOwner(shooter);
                                                        entityToSpawn.setBaseDamage(damage);
                                                        entityToSpawn.setKnockback(knockback);
                                                        entityToSpawn.setSilent(true);
                                                        entityToSpawn.setPierceLevel(piercing);
                                                        entityToSpawn.setCritArrow(true);
                                                        return entityToSpawn;
                                                    }
                                                }.getArrow(projectileLevel, entity,
                                                        (float) (entity instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity3.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0), 0, (byte) 1);
                                                _entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
                                                _entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, (float) 1.5, 0);
                                                projectileLevel.addFreshEntity(_entityToSpawn);
                                            }
                                        }
                                        if (entity instanceof LivingEntity _entity)
                                            _entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1));
                                        if (world instanceof ServerLevel _level)
                                            _level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, (y + 1.5), z, 64, 1.5, 1.5, 1.5, 0.2);
                                    }
                                    rep = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 96, 96, 96), e1 -> true).stream().sorted(new Object() {
                                        Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                            return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                        }
                                    }.compareDistOf(x, y, z)).findFirst().orElse(null);
                                    if (!(rep == null) && rep.isAlive() && (rep instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (rep instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
                                        if (rep instanceof LivingEntity _entity)
                                            _entity.setHealth((float) ((rep instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (rep instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1));
                                        if (world instanceof ServerLevel _level)
                                            _level.sendParticles(ParticleTypes.HAPPY_VILLAGER, (rep.getX()), (rep.getY() + 1.5), (rep.getZ()), 64, 1.5, 1.5, 1.5, 0.2);
                                    }
                                });
                                if (entity instanceof TideBishopEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(TideBishopEntity.DATA_skillp, 200);
                            }
                        } else {
                            if (entity instanceof TideBishopEntity _datEntSetI)
                                _datEntSetI.getEntityData().set(TideBishopEntity.DATA_skillp, (int) (sklp - 1));
                        }
                    }
                }
                nearest = world.getEntitiesOfClass(TideDeathrepellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 128, 128, 128), e -> true).stream().sorted(new Object() {
					Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
						return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
					}
				}.compareDistOf(x, y, z)).findFirst().orElse(null);
				if (!(nearest == null) && nearest instanceof LivingEntity _livEnt21 && _livEnt21.hasEffect(CaerulaArborModMobEffects.FAKE_DEATH.get())) {
					EntityUtils.spawnLinkParticles(world, entity, nearest);
					if (CaerulaArborModVariables.MapVariables.get(world).strategy_silence >= 3) {
						if (entity instanceof LivingEntity _livingEntity22 && _livingEntity22.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
							_livingEntity22.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(30);
					} else if (CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting >= 4) {
						if (entity instanceof LivingEntity _livingEntity23 && _livingEntity23.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
							_livingEntity23.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(15);
					}
				} else {
					if (entity instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
						_livingEntity24.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(0);
				}
			}
		}
	}
}