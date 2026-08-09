package com.susen36.caerulaarbor.entity.base;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

import static com.susen36.caerulaarbor.util.EntityUtils.SEA_BORN_BOSS;
import static com.susen36.caerulaarbor.util.EntityUtils.SEA_BORN_MINION;

public abstract class SeaMonster extends Monster implements GeoEntity, SyncedAnimationEntity {
	private static final TagKey<Block> NETHERSEA_WALKER = BlockTags.create(
			ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_functions"));

	private static final ResourceLocation SILENCE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "silence_movement_speed");
	private static final ResourceLocation BOOST_ATTACK_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "boost_of_silence_attack_damage");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected SeaMonster(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void die(DamageSource source) {
		if (!this.level().isClientSide()
				&& !this.getPersistentData().getBoolean("caerula.sublimationRevived")
				&& !this.getType().is(SEA_BORN_BOSS)
				&& !this.getType().is(SEA_BORN_MINION)) {
			MapVariables vars = MapVariables.get(this.level());
			double subl = vars.strategy_sublimation;
			if (subl <= 0.0) {
				super.die(source);
			} else {
				double finalBreed = Math.min(subl, vars.strategy_breed);
				double rate = 0.05 + 0.05 * finalBreed;
				if (this.random.nextDouble() >= rate) {
					super.die(source);
				} else {
					this.getPersistentData().putBoolean("caerula.sublimationRevived", true);
					this.level().playSound(null, this.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.5f, 1.0f);
					if (this.level() instanceof ServerLevel slevel) {
						slevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getY() + 1.0, this.getZ(), 32, 1.0, 1.0, 1.0, 0.15);
						double revivalRate = finalBreed == 3.0 ? 0.1 : finalBreed == 4.0 ? 0.2 : 0.0;
						if (revivalRate > 0.0 && this.random.nextDouble() < revivalRate) {
							Entity spawned = CAEntities.CAERULA_OFFSPRING.get().spawn(slevel, this.blockPosition(), MobSpawnType.MOB_SUMMONED);
							if (spawned != null) {
								spawned.setYRot(this.random.nextFloat() * 360.0F);
							}
						}
					}
					float reviveHealth = this.getMaxHealth() * (float) (0.1 + 0.05 * finalBreed);
					this.setHealth(reviveHealth);
				}
			}
		} else {
			super.die(source);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide()) {
			MapVariables variables = MapVariables.get(this.level());
			double silenceLevel = variables.strategy_silence;

			if (silenceLevel > 0) {
				int amplifier = (int) (silenceLevel - 1);
				AttributeInstance attackAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
				if (silenceLevel >= 3) {
					if (attackAttr.getModifier(BOOST_ATTACK_ID) == null) {
						double attackBonus = 0.25D * silenceLevel;
						attackAttr.addTransientModifier(new AttributeModifier(BOOST_ATTACK_ID, attackBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
					}
					if (!this.hasEffect(CAMobEffects.STRENGTH_OF_CROWD)) {
						double amplifi = -1;
						double range = silenceLevel >= 4 ? 64 : 32;
						double maxAmp = silenceLevel >= 4 ? 29 : 9;
						int ampStep = silenceLevel >= 4 ? 2 : 1;
						Vec3 center = this.position();
						List<LivingEntity> entfound = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(range / 2d),
								e -> e != this && e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born"))));
						for (LivingEntity ignored : entfound) {
							amplifi = amplifi + ampStep;
							if (amplifi >= maxAmp) {
								amplifi = maxAmp;
								break;
							}
						}
						if (amplifi >= 0) {
							this.addEffect(new MobEffectInstance(CAMobEffects.STRENGTH_OF_CROWD, -1, (int) amplifi, false, false));
						}
					}
				} else if (this.getHealth() < this.getMaxHealth() * 0.5) {
					if (attackAttr.getModifier(BOOST_ATTACK_ID) == null) {
						double attackBonus = 0.25D * silenceLevel;
						attackAttr.addTransientModifier(new AttributeModifier(BOOST_ATTACK_ID, attackBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
					}
				} else {
					if (attackAttr.getModifier(BOOST_ATTACK_ID) != null) {
						attackAttr.removeModifier(BOOST_ATTACK_ID);
					}
					this.removeEffect(CAMobEffects.STRENGTH_OF_CROWD);
				}

				if (this.tickCount % 10 == 0) {
					AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
					if (this.isAggressive()) {
						if (speedAttr.getModifier(SILENCE_SPEED_ID) == null) {
							double speedBonus = 0.05D * silenceLevel;
							speedAttr.addTransientModifier(new AttributeModifier(SILENCE_SPEED_ID, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						}
					} else if (speedAttr.getModifier(SILENCE_SPEED_ID) != null) {
						speedAttr.removeModifier(SILENCE_SPEED_ID);
					}
				}

				if (!this.isAggressive() && this.getHealth() < this.getMaxHealth()) {
					int interval = 50 >> amplifier;
					if (this.tickCount % interval == 0) {
						this.heal(1.0F);
					}
				}
			}
		}
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
		SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
		if (!world.isClientSide()) {
			double breedLevel = MapVariables.get(world).strategy_breed;
			if (breedLevel > 0 && !this.isRemoved() && (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) && !this.getType().is(SEA_BORN_BOSS) && !this.getType().is(SEA_BORN_MINION)) {
				BreedGroupData breedData;
				if (livingdata instanceof BreedGroupData existing) {
					existing.groupSize++;
					breedData = existing;
				} else {
					breedData = new BreedGroupData(1);
					retval = breedData;
				}
				ServerLevel level = world.getLevel();
				double x = this.getX();
				double y = this.getY();
				double z = this.getZ();
				RandomSource random = this.getRandom();

				if (breedData.groupSize < 5 && random.nextDouble() < 0.05 + 0.05 * breedLevel) {
					Entity dup = this.getType().create(level);
					if (dup != null) {
						double ox = Mth.nextDouble(random, -1, 1);
						double oz = Mth.nextDouble(random, -1, 1);
						dup.setPos(x + ox, y, z + oz);
						if (dup instanceof Mob mob) {
							mob.finalizeSpawn(level, level.getCurrentDifficultyAt(dup.blockPosition()), MobSpawnType.SPAWNER, breedData);
						}
						level.addFreshEntity(dup);
						breedData.groupSize++;
					}
				}

				if (breedLevel >= 3 && breedData.groupSize < 5 && random.nextDouble() < 0.05 * (breedLevel - 2)) {
					Entity dup = this.getType().create(level);
					if (dup != null) {
						double ox = Mth.nextDouble(random, -1, 1);
						double oz = Mth.nextDouble(random, -1, 1);
						dup.setPos(x + ox, y, z + oz);
						if (dup instanceof Mob mob) {
							mob.finalizeSpawn(level, level.getCurrentDifficultyAt(dup.blockPosition()), MobSpawnType.SPAWNER, breedData);
						}
						level.addFreshEntity(dup);
						breedData.groupSize++;
					}
				}
			}
		}
		return retval;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	protected float getBlockSpeedFactor() {
		float baseFactor = super.getBlockSpeedFactor();
		BlockPos belowPos = this.getBlockPosBelowThatAffectsMyMovement();
		BlockState belowState = this.level().getBlockState(belowPos);
		if (belowState.is(NETHERSEA_WALKER)) {
			double migrationLevel = MapVariables.get(this.level()).strategy_migration;
			if (migrationLevel > 0.0) {
				return baseFactor * (float) (1.0 + 0.05 * migrationLevel);
			}
		}
		return baseFactor;
	}

	public static class BreedGroupData implements SpawnGroupData {
		public int groupSize;

		public BreedGroupData(int groupSize) {
			this.groupSize = groupSize;
		}
	}
}