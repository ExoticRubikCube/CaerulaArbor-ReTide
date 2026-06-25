package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.IsharmlaDroppedAttackProcedure;
import com.apocalypse.caerulaarbor.procedures.ShieldBreakProcedure;
import com.apocalypse.caerulaarbor.procedures.SuperCatRangedProcedure;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.apocalypse.caerulaarbor.utils.EntityPredicateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class MobAttackDelayHandler {

	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getSource(), event.getEntity(), event.getSource().getDirectEntity(), event.getSource().getEntity());
		}
	}

	private static void execute(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity, Entity immediatesourceentity, Entity sourceentity) {
		if (damagesource == null || entity == null || sourceentity == null)
			return;
		if (world.isClientSide()) {
			return;
		}
		if (!damagesource.is(DamageTypes.MOB_ATTACK)) {
			return;
		}

		if (sourceentity instanceof NucleicMaleficentEntity) { handleNucleicMaleficent(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof SuperBigCatEntity) { handleSuperBigCat(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedCatEntity) { handleOceanizedCat(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedWardenEntity || sourceentity instanceof OceanizedWardenisEntity) { handleOceanizedWarden(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof CrackerAbyssalEntity) { handleCrackerAbyssal(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof TheAbandonedEntity) { handleTheAbandoned(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof CorrectinalPhalaxVanguardEntity) { handleCorrectionalVanguard(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof CorrectionalPhalanxyInfantryEntity) { handleCorrectionalInfantry(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof WarriorPriestEntity) { handleWarriorPriest(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof JuniorWarriorPriestEntity) { handleJuniorWarriorPriest(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof IzumikEntity) { handleIzumik(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedRavagerEntity) { handleOceanizedRavager(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedEndermanEntity) { handleOceanizedEnderman(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedSpiderEntity) { handleOceanizedSpider(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof OceanizedBruteEntity) { handleOceanizedBrute(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof FlamarineGolemEntity) { handleFlamarineGolem(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof FlamarineStatueEntity) { handleFlamarineStatue(event, world, entity, sourceentity); return; }
		if (sourceentity instanceof SaintCarmenEntity) { handleSaintCarmen(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof SkadiCorruptedEntity) { handleSkadiCorrupted(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof TideChimeraEntity) { handleTideChimera(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof IreneEntity) { handleIrene(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof SpecterEntity) { handleSpecter(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof GladiiaEntity && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack")))) { handleGladiia(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof UlpiansEntity && !damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack")))) { handleUlpians(event, world, x, y, z, entity, sourceentity); return; }
		if (sourceentity instanceof IsharmlaEntity) { handleIsharmla(event, world, x, y, z, entity, sourceentity); return; }
	}

	private static void cancelEvent(LivingAttackEvent event) {
		if (event != null && event.isCancelable()) {
			event.setCanceled(true);
		}
	}

	private static float getAttackDamage(Entity sourceentity) {
		return (float) (sourceentity instanceof LivingEntity _livingEntity && _livingEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0);
	}

	private static DamageSource seabornAttack(LevelAccessor world, Entity sourceentity) {
		return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "general_seaborn_attack"))), sourceentity);
	}

	private static DamageSource warriorAttack(LevelAccessor world, Entity sourceentity) {
		return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "generic_warrior_attack"))), sourceentity);
	}

	private static void playSound(LevelAccessor world, double x, double y, double z, String sound, SoundSource source, float volume, float pitch) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sound)), source, volume, pitch);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sound)), source, volume, pitch, false);
			}
		}
	}

	private static void handleNucleicMaleficent(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(12, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.5) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleSuperBigCat(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		playSound(world, x, y, z, "entity.cat.hiss", SoundSource.HOSTILE, 1, (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 10) {
					SuperCatRangedProcedure.execute(world, x, y, z, sourceentity);
				}
			}
		});
		CaerulaArborMod.queueServerWork(14, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 15) {
					SuperCatRangedProcedure.execute(world, x, y, z, sourceentity);
				}
			}
		});
	}

	private static void handleOceanizedCat(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		playSound(world, x, y, z, "entity.cat.hiss", SoundSource.HOSTILE, (float) 0.75, (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
		CaerulaArborMod.queueServerWork(14, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleOceanizedWarden(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(10, () -> {
			if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 6) {
				playSound(world, x, y, z, "entity.warden.attack_impact", SoundSource.HOSTILE, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1), 1);
				EntityUtils.wardenRangedAttack(world, sourceentity, false, 1, x, y, z);
			}
		});
	}

	private static void handleCrackerAbyssal(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(12, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
		playSound(world, x, y, z, "caerula_arbor:reefbreaker_attack", SoundSource.HOSTILE, 10, (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
		double amplifi = sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.REEF_CRACKER.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.REEF_CRACKER.get()).getAmplifier() : 0;
		if (sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.REEF_CRACKER.get())) {
			if (amplifi < 14) {
				if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REEF_CRACKER.get(), 120, (int) (amplifi + 1), false, false));
			} else {
				if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REEF_CRACKER.get(), 120, 14, false, false));
			}
		} else {
			if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.REEF_CRACKER.get(), 120, 0, false, false));
		}
	}

	private static void handleTheAbandoned(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(10, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleCorrectionalVanguard(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3.5) {
					entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
		CaerulaArborMod.queueServerWork(14, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 4.5) {
					entity.hurt(warriorAttack(world, sourceentity), (float) (getAttackDamage(sourceentity) * 1.25));
				}
			}
		});
	}

	private static void handleCorrectionalInfantry(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(10, () -> {
			entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
		});
	}

	private static void handleWarriorPriest(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(8, () -> {
			entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, true));
		});
	}

	private static void handleJuniorWarriorPriest(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(11, () -> {
			entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
		});
	}

	private static void handleIzumik(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(7, () -> {
			playSound(world, x, y, z, "caerula_arbor:izumik_attack", SoundSource.HOSTILE, 1, (float) Mth.nextDouble(RandomSource.create(), 0.85, 0.15));
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 13) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "izumik_normal_attack"))), sourceentity),
							getAttackDamage(sourceentity));
					if ((sourceentity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_phase) : 0) >= 1) {
						if (CaerulaArborModVariables.MapVariables.get(world).strategy_grow >= 4) {
							entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), sourceentity),
									(float) (getAttackDamage(sourceentity) * 1.5));
						} else {
							entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "ocean_magic"))), sourceentity),
									getAttackDamage(sourceentity));
						}
						if ((sourceentity instanceof IzumikEntity _datEntI ? _datEntI.getEntityData().get(IzumikEntity.DATA_phase) : 0) >= 2 && Math.random() < 0.15) {
							if (entity instanceof LivingEntity _livingEntity && _livingEntity.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()))
								_livingEntity.getAttribute(CaerulaArborModAttributes.NUMB.get())
										.setBaseValue(((entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get())
												? _livingEntity2.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue()
												: 0) + 1));
							if (world instanceof ServerLevel _level)
								_level.sendParticles(ParticleTypes.FIREWORK, x, (y + 0.75), z, 16, 0.75, 0.75, 0.75, 0.1);
						}
					}
				}
			}
		});
	}

	private static void handleOceanizedRavager(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(11, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3.8) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleOceanizedEnderman(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.5) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleOceanizedSpider(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(10, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.6) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleOceanizedBrute(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(10, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.6) {
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleFlamarineGolem(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(20, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 5.75) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), sourceentity),
							getAttackDamage(sourceentity));
					ShieldBreakProcedure.execute(world, x, y, z, entity, 120);
				}
			}
		});
		if (sourceentity instanceof FlamarineGolemEntity _datEntSetI)
			_datEntSetI.getEntityData().set(FlamarineGolemEntity.DATA_duration, 35);
	}

	private static void handleFlamarineStatue(LivingAttackEvent event, LevelAccessor world, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(12, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.5) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "golem_attack"))), sourceentity),
							getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleSaintCarmen(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					playSound(world, x, y, z, "caerula_arbor:carmen_melee", SoundSource.NEUTRAL, (float) 2.33, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleSkadiCorrupted(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(24, () -> {
			if (sourceentity.isAlive() && EntityPredicateUtils.isCorruptedDurative(sourceentity)) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 2.25) {
					if (sourceentity != null) {
						double d = sourceentity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity0.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
						{
							final Vec3 _center = new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()));
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (!(entityiterator instanceof LivingEntity)) {
									continue;
								}
								if (sourceentity == entityiterator) {
									continue;
								}
								if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
									continue;
								}
								if ((entityiterator != null ? sourceentity.distanceTo(entityiterator) : -1) <= 3) {
									entityiterator.hurt(seabornAttack(world, sourceentity), (float) d);
									if (sourceentity == null || entityiterator == null)
										continue;
									Vec3 pushVec = sourceentity.position().vectorTo(entityiterator.position());
									if (pushVec.lengthSqr() < 0.0001) pushVec = new Vec3(0, 0, 1);
									else pushVec = pushVec.normalize();
									pushVec = pushVec.scale(1.25);
									entityiterator.push(pushVec.x, pushVec.y, pushVec.z);
								}
							}
						}
					}
					if (sourceentity instanceof SkadiCorruptedEntity _datEntSetI)
						_datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_duration, 40);
				}
			}
		});
	}

	private static void handleTideChimera(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		CaerulaArborMod.queueServerWork(8, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 4) {
					EntityUtils.giveLessArmor(entity, 11);
					playSound(world, x, y, z, "caerula_arbor:puncturefish_attack", SoundSource.HOSTILE, 3, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					entity.hurt(seabornAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleIrene(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		if (sourceentity instanceof IreneEntity _datEntSetI)
			_datEntSetI.getEntityData().set(IreneEntity.DATA_skillp1, (int) ((sourceentity instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(IreneEntity.DATA_skillp1) : 0) + 1));
		if (sourceentity instanceof IreneEntity _datEntSetI)
			_datEntSetI.getEntityData().set(IreneEntity.DATA_skillp2, (int) ((sourceentity instanceof IreneEntity _datEntI ? _datEntI.getEntityData().get(IreneEntity.DATA_skillp2) : 0) + 1));
		CaerulaArborMod.queueServerWork(6, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					playSound(world, x, y, z, "caerula_arbor:irene_attack", SoundSource.NEUTRAL, (float) 2.5, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.MUTE.get(), 60, 0, false, false));
					entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
		CaerulaArborMod.queueServerWork(11, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 3) {
					playSound(world, x, y, z, "caerula_arbor:irene_attack", SoundSource.NEUTRAL, (float) 2.5, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
					entity.hurt(warriorAttack(world, sourceentity), getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleSpecter(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		if (sourceentity instanceof SpecterEntity _datEntSetI)
			_datEntSetI.getEntityData().set(SpecterEntity.DATA_duration, (int) ((sourceentity instanceof SpecterEntity _datEntI ? _datEntI.getEntityData().get(SpecterEntity.DATA_duration) : 0) + 30));
		if (sourceentity instanceof SpecterEntity _datEntSetI)
			_datEntSetI.getEntityData().set(SpecterEntity.DATA_skillp1, (int) ((sourceentity instanceof SpecterEntity _datEntI ? _datEntI.getEntityData().get(SpecterEntity.DATA_skillp1) : 0) + 1));
		playSound(world, x, y, z, "caerula_arbor:specter_attack", SoundSource.HOSTILE, (float) 2.5, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
		CaerulaArborMod.queueServerWork(12, () -> {
			if (sourceentity.isAlive()) {
				if (entity == null || sourceentity == null)
					return;
				if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 3.5) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "saw_cut"))), sourceentity),
							(float) (getAttackDamage(sourceentity) * 0.45));
				}
				CaerulaArborMod.queueServerWork(3, () -> {
					if (sourceentity.isAlive()) {
						if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 3.5) {
							entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "saw_cut"))), sourceentity),
									(float) (getAttackDamage(sourceentity) * 0.5));
						}
					}
				});
				CaerulaArborMod.queueServerWork(6, () -> {
					if (sourceentity.isAlive()) {
						if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 3.5) {
							entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "saw_cut"))), sourceentity),
									(float) (getAttackDamage(sourceentity) * 0.45));
						}
					}
				});
			}
		});
	}

	private static void handleGladiia(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		playSound(world, x, y, z, "caerula_arbor:gladiia_attack_pre", SoundSource.NEUTRAL, (float) 2.2, (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
		CaerulaArborMod.queueServerWork(9, () -> {
			if (sourceentity.isAlive()) {
				if ((entity != null ? sourceentity.distanceTo(entity) : -1) <= 5) {
					playSound(world, x, y, z, "caerula_arbor:gladiia_attack_hit", SoundSource.NEUTRAL, (float) 2.75, 1);
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), sourceentity),
							getAttackDamage(sourceentity));
				}
			}
		});
	}

	private static void handleUlpians(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		if (sourceentity instanceof UlpiansEntity _datEntSetI)
			_datEntSetI.getEntityData().set(UlpiansEntity.DATA_duration, (int) ((sourceentity instanceof UlpiansEntity _datEntI ? _datEntI.getEntityData().get(UlpiansEntity.DATA_duration) : 0) + 30));
		playSound(world, x, y, z, "caerula_arbor:anchor_pre", SoundSource.HOSTILE, (float) 2.2, 1);
		CaerulaArborMod.queueServerWork(14, () -> {
			if (sourceentity.isAlive()) {
				if (sourceentity == null)
					return;
				Entity enemy = null;
				double damage = 0;
				double r = 0;
				enemy = sourceentity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
				r = 24;
				damage = sourceentity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity1.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
				playSound(world, x, y, z, "caerula_arbor:anchor_hit", SoundSource.HOSTILE, (float) 2.75, 1);
				{
					final Vec3 _center = new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!(entityiterator instanceof LivingEntity)) {
							continue;
						}
						if (!entityiterator.isAlive()) {
							continue;
						}
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof ServerPlayer _serverPlayer && _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE) {
								continue;
							}
							if (entityiterator instanceof ServerPlayer _serverPlayer && _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
								continue;
							}
						}
						if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
							if (!(entityiterator == enemy)) {
								continue;
							}
						}
						if (entityiterator == sourceentity) {
							continue;
						}
						if (sourceentity.distanceTo(entityiterator) <= r) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), sourceentity),
									(float) damage);
							Vec3 pushVec = sourceentity.position().vectorTo(entityiterator.position());
							if (pushVec.lengthSqr() < 0.0001) pushVec = new Vec3(0, 0, 1);
							else pushVec = pushVec.normalize();
							pushVec = pushVec.scale(1.25);
							entityiterator.push(pushVec.x, pushVec.y, pushVec.z);
						}
					}
				}
			}
		});
	}

	private static void handleIsharmla(LivingAttackEvent event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		cancelEvent(event);
		playSound(world, sourceentity.getX(), sourceentity.getY(), sourceentity.getZ(), "caerula_arbor:isharmla_attack_pre", SoundSource.HOSTILE, (float) 2.5, 1);
		CaerulaArborMod.queueServerWork(13, () -> {
			if (sourceentity.isAlive()) {
				double sX = sourceentity.getX();
				double sY = sourceentity.getY();
				double sZ = sourceentity.getZ();
				for (int index0 = 0; index0 < 12; index0++) {
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.MOIST_BOOM.get()), sX, (sY + 10 + index0), sZ, 6, (index0 * 0.1), (index0 * 0.1), (index0 * 0.1), 0);
				}
			}
		});
		CaerulaArborMod.queueServerWork(15, () -> {
			if (sourceentity.isAlive()) {
				if (sourceentity.distanceTo(entity) <= 32) {
					Entity enemy = null;
					double damage = 0;
					double r = 0;
					double count = 0;
					double sX = 0;
					double sY = 0;
					double sZ = 0;
					enemy = sourceentity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
					r = 24;
					damage = sourceentity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity1.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
					sX = sourceentity.getX();
					sY = sourceentity.getY();
					sZ = sourceentity.getZ();
					playSound(world, x, y, z, "caerula_arbor:isharmla_attack_launch", SoundSource.HOSTILE, (float) 2.5, 1);
					IsharmlaDroppedAttackProcedure.execute(world, x, y, z, sourceentity, Mth.nextDouble(RandomSource.create(), 1.5, 3), 1);
					for (int index0 = 0; index0 < 5; index0++) {
						if (count > 5) {
							break;
						}
						{
							final Vec3 _center = new Vec3((sourceentity.getX()), (sourceentity.getY()), (sourceentity.getZ()));
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (count > 5) {
									break;
								}
								if (!(entityiterator instanceof LivingEntity)) {
									continue;
								}
								if (!entityiterator.isAlive()) {
									continue;
								}
								if (entityiterator instanceof Player) {
									if (new Object() {
										public boolean checkGamemode(Entity _ent) {
											if (_ent instanceof ServerPlayer _serverPlayer) {
												return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
											} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
												return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
														&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
											}
											return false;
										}
									}.checkGamemode(entityiterator)) {
										continue;
									}
									if (new Object() {
										public boolean checkGamemode(Entity _ent) {
											if (_ent instanceof ServerPlayer _serverPlayer) {
												return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
											} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
												return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
														&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
											}
											return false;
										}
									}.checkGamemode(entityiterator)) {
										continue;
									}
								}
								if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
									if (!(entityiterator == enemy)) {
										continue;
									}
								}
								if (entityiterator == sourceentity) {
									continue;
								}
								if (sourceentity.distanceTo(entityiterator) <= r) {
									count = count + 1;
									CaerulaArborMod.queueServerWork((int) (2 * count), () -> {
										IsharmlaDroppedAttackProcedure.execute(world, entityiterator.getX(), entityiterator.getY(), entityiterator.getZ(), sourceentity, Mth.nextDouble(RandomSource.create(), 1.5, 3), 1);
									});
								}
							}
						}
					}
				}
			}
		});
		if (sourceentity instanceof IsharmlaEntity _datEntSetI)
			_datEntSetI.getEntityData().set(IsharmlaEntity.DATA_DURATION, 60);
	}
}