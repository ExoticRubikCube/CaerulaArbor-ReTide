package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAConfigs;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.manager.SeabornSpawnManager;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EndspeakerEntity extends SeaMonster {
	protected static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.STRING);
	protected static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Boolean> DATA_IS_EVOLVING = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Integer> DATA_EVOLVE_TIME = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> DATA_SKILL_COOLDOWN = SynchedEntityData.defineId(EndspeakerEntity.class, EntityDataSerializers.INT);

	public String animationprocedure = "empty";
	protected String prevAnim = "empty";
	protected boolean swinging;
	protected long lastSwing;

	private final ServerBossEvent bossInfo;

	public EndspeakerEntity(Level level) {
		this(CAEntities.ENDSPEAKER.get(), level);
	}

	public EndspeakerEntity(EntityType<? extends EndspeakerEntity> entityType, Level level) {
		super(entityType, level);
		this.bossInfo = new ServerBossEvent(this.getBossBarName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.PROGRESS);
		this.setNoAi(false);
		this.setPersistenceRequired();
	}

	public static boolean hasAbility(LevelAccessor world, double index) {
		int comparator = 1 << (int) index;
		int abilities = (int) MapVariables.get(world).endspeaker_abolities;
		return (abilities & comparator) == comparator;
	}

	public boolean hasAbility(double index) {
		return hasAbility(this.level(), index);
	}

	@Nullable
	public static EndspeakerEntity spawnForPhase(ServerLevel level, BlockPos pos, MobSpawnType spawnType, int phase) {
		EndspeakerEntity endspeaker = CAEntities.ENDSPEAKER.get().create(level);
		if (endspeaker == null) {
			return null;
		}
		endspeaker.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
		endspeaker.setPhase(Mth.clamp(phase, 0, 3));
		endspeaker.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), spawnType, null, null);
		endspeaker.setHealth(endspeaker.getMaxHealth());
		level.addFreshEntity(endspeaker);
		return endspeaker;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.15)
				.add(Attributes.MAX_HEALTH, 16.0)
				.add(Attributes.ARMOR, 0.0)
				.add(Attributes.ATTACK_DAMAGE, 1.0)
				.add(Attributes.FOLLOW_RANGE, 16.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
	}

	protected int getFloatGoalPriority() {
		if (this.getPhase() == 0) {
			return 4;
		}
		if (this.getPhase() == 1) {
			return 18;
		}
		if (this.getPhase() == 2) {
			return 16;
		}
		if (!this.hasNextPhase()) {
			return 17;
		}
		return 0;
	}

	protected boolean hasNextPhase() {
        return this.getPhase() == 0 || this.getPhase() == 1 || this.getPhase() == 2;
    }

	protected boolean canTransitionToNextPhase() {
		return this.hasNextPhase() && !this.isEvolving();
	}

	public int getPhase() {
		return this.entityData.get(DATA_PHASE);
	}

	public void setPhase(int phase) {
		this.entityData.set(DATA_PHASE, Mth.clamp(phase, 0, 3));
		this.updatePhaseRuntimeProperties();
		this.refreshDimensions();
	}

	public int getEvolveTime() {
		return this.entityData.get(DATA_EVOLVE_TIME);
	}

	public void setEvolveTime(int evolveTime) {
		this.entityData.set(DATA_EVOLVE_TIME, evolveTime);
	}

	protected boolean isEvolving() {
		return this.getEvolveTime() > 0;
	}

	public int getDuration() {
		return this.entityData.get(DATA_DURATION);
	}

	public void setDuration(int duration) {
		this.entityData.set(DATA_DURATION, duration);
	}

	public int getSkillCooldown() {
		return this.entityData.get(DATA_SKILL_COOLDOWN);
	}

	public void setSkillCooldown(int skillCooldown) {
		this.entityData.set(DATA_SKILL_COOLDOWN, skillCooldown);
	}

	protected boolean isPhaseZeroStarting() {
		return this.tickCount >= 28 && !this.isEvolving();
	}

	protected boolean isPhaseOneStarting() {
		return this.tickCount >= 50 && !this.isEvolving();
	}

	protected boolean isPhaseTwoDurative() {
		return !this.isEvolving() && this.getDuration() <= 0 && this.tickCount >= 68;
	}

	protected boolean isPhaseThreeDurative() {
		return this.isAlive() && this.getDuration() <= 0 && this.tickCount >= 50;
	}

	protected ServerBossEvent.BossBarOverlay getPhaseBossBarOverlay() {
		return switch (this.getPhase()) {
			case 2 -> ServerBossEvent.BossBarOverlay.NOTCHED_6;
			case 3 -> ServerBossEvent.BossBarOverlay.NOTCHED_10;
			default -> ServerBossEvent.BossBarOverlay.PROGRESS;
		};
	}

	protected Component getBossBarName() {
		return this.getTypeName();
	}

	protected void updatePhaseRuntimeProperties() {
		this.xpReward = switch (this.getPhase()) {
			case 2 -> 48;
			case 3 -> 64;
			default -> 0;
		};
		this.setMaxUpStep(switch (this.getPhase()) {
			case 2 -> 1.0F;
			case 3 -> 1.5F;
			default -> 0.6F;
		});
		this.updatePhaseAttributes();
		if (this.bossInfo != null) {
			this.bossInfo.setColor(this.getPhase() >= 2 ? ServerBossEvent.BossBarColor.WHITE : ServerBossEvent.BossBarColor.BLUE);
			this.bossInfo.setOverlay(this.getPhaseBossBarOverlay());
			this.bossInfo.setName(this.getBossBarName());
			float maxHealth = this.getMaxHealth();
			float progress = maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F);
			this.bossInfo.setProgress(progress);
		}
	}

	private void setAttributeBaseValue(Attribute attribute, double value) {
		AttributeInstance instance = this.getAttribute(attribute);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}

	protected void updatePhaseAttributes() {
		double maxHealth = switch (this.getPhase()) {
			case 1 -> 120.0D;
			case 2 -> 140.0D;
			case 3 -> 224.0D;
			default -> 16.0D;
		};
		this.setAttributeBaseValue(Attributes.MAX_HEALTH, maxHealth);

		this.setAttributeBaseValue(Attributes.MOVEMENT_SPEED, switch (this.getPhase()) {
			case 1, 3 -> 0.16D;
			case 2 -> 0.18D;
            default -> 0.15D;
		});

		this.setAttributeBaseValue(Attributes.ARMOR, switch (this.getPhase()) {
			case 1 -> 5.0D;
			case 2 -> 6.0D;
			case 3 -> 8.0D;
			default -> 0.0D;
		});

		this.setAttributeBaseValue(Attributes.ATTACK_DAMAGE, switch (this.getPhase()) {
			case 1 -> 7.0D;
			case 2 -> 9.0D;
			case 3 -> 11.0D;
			default -> 1.0D;
		});

		this.setAttributeBaseValue(Attributes.FOLLOW_RANGE, switch (this.getPhase()) {
			case 2 -> 32.0D;
			case 1, 3 -> 36.0D;
			default -> 16.0D;
		});

		this.setAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE, this.getPhase() == 0 ? 0.0D : 10.0D);

		if (this.getHealth() > maxHealth) {
			this.setHealth((float) maxHealth);
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_ANIMATION, "undefined");
		this.entityData.define(DATA_PHASE, 0);
		this.entityData.define(DATA_IS_EVOLVING, false);
		this.entityData.define(DATA_EVOLVE_TIME, 0);
		this.entityData.define(DATA_DURATION, 0);
		this.entityData.define(DATA_SKILL_COOLDOWN, 0);
	}

	@Override
	protected Component getTypeName() {
		return switch (this.getPhase()) {
			case 1 -> Component.translatable("entity.caerula_arbor.endspeaker_1");
			case 2 -> Component.translatable("entity.caerula_arbor.endspeaker_2");
			case 3 -> Component.translatable("entity.caerula_arbor.endspeaker_3");
			default -> Component.translatable("entity.caerula_arbor.endspeaker_0");
		};
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(this.getFloatGoalPriority(), new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}
		});
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}
		});
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseZeroStarting();
			}
		});
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.goalSelector.addGoal(2, new BreakDoorGoal(this, entity -> true) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseOneStarting() && WorldUtils.canGrief(EndspeakerEntity.this.level());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseOneStarting() && WorldUtils.canGrief(EndspeakerEntity.this.level());
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25D, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 20.25D;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}
		});
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 25.0D;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}
		});
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4.0D;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}
		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Villager.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Witch.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative());
			}
		});
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				return super.canUse()
						&& (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative())
						&& EntityUtils.isOceanizedPlayerNearby(EndspeakerEntity.this.level(), EndspeakerEntity.this.getX(), EndspeakerEntity.this.getY(), EndspeakerEntity.this.getZ());
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse()
						&& (EndspeakerEntity.this.isPhaseOneStarting() || EndspeakerEntity.this.isPhaseTwoDurative() || EndspeakerEntity.this.isPhaseThreeDurative())
						&& EntityUtils.isOceanizedPlayerNearby(EndspeakerEntity.this.level(), EndspeakerEntity.this.getX(), EndspeakerEntity.this.getY(), EndspeakerEntity.this.getZ());
			}
		});
		this.goalSelector.addGoal(14, new RandomStrollGoal(this, 1.0D) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}
		});
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseTwoDurative();
			}
		});
		this.goalSelector.addGoal(15, new RandomStrollGoal(this, 1.0D) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}
		});
		this.goalSelector.addGoal(16, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseThreeDurative();
			}
		});
		this.goalSelector.addGoal(16, new RandomStrollGoal(this, 1.0D) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}
		});
		this.goalSelector.addGoal(17, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && EndspeakerEntity.this.isPhaseOneStarting();
			}
		});
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return switch (this.getPhase()) {
			case 1 -> EntityDimensions.scalable(0.75F, 1.5F);
			case 2 -> EntityDimensions.scalable(1.2F, 2.8F);
			case 3 -> EntityDimensions.scalable(1.0F, 3.375F);
			default -> EntityDimensions.scalable(0.65F, 0.7F);
		};
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return CASounds.SEABORN_GENERIC_HIT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		if (this.getPhase() == 0) {
			return CASounds.SEABORN_GENERIC_HIT.get();
		}else if (this.getPhase() == 1) {
			return CASounds.SEABORN_DEATH.get();
		}else if (this.getPhase() == 2) {
			return CASounds.SEABORN_DEATH.get();
		}else if (!this.hasNextPhase()) {
			return CASounds.SEABORN_DEATH.get();
		}
		return super.getDeathSound();
	}

	@Override
	public SoundEvent getAmbientSound() {
		if (this.getPhase() == 2) {
			return SoundEvents.GLOW_SQUID_AMBIENT;
		}
		return super.getAmbientSound();
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockState) {
		if (this.getPhase() == 1) {
			this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
			return;
		}
		super.playStepSound(pos, blockState);
	}

	@Override
	public boolean isPushable() {
		return this.getPhase() != 2 && super.isPushable();
	}

	@Override
	protected void doPush(Entity entity) {
		if (this.getPhase() != 2) {
			super.doPush(entity);
		}
	}

	@Override
	protected void pushEntities() {
		if (this.getPhase() != 2) {
			super.pushEntities();
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FALL)) {
			return false;
		}
		if (source.is(DamageTypes.DROWN)) {
			return false;
		}
		if (this.hasAbility(3)) {
			Entity sourceEntity = source.getEntity();
			if (this.hasEffect(CAMobEffects.TRAIL_BUFF.get()) || sourceEntity instanceof LivingEntity livingSource && livingSource.hasEffect(CAMobEffects.TRAIL_BUFF.get())) {
				amount *= 0.65F;
			}
		}
		return super.hurt(source, amount);
	}

	private boolean hurtWithEndspeakerAttack(LivingEntity target, float amount) {
		if (this.hasAbility(3) && target.hasEffect(CAMobEffects.TRAIL_BUFF.get())) {
			amount *= 1.5F;
		}
		boolean damaged = target.hurt(
				CADamageTypes.source(this.level(), CADamageTypes.ENDSPEAKER_ATTACK, this), amount);
		if (damaged && this.hasAbility(5) && !this.level().isClientSide()) {
			int amplifier = this.hasEffect(CAMobEffects.REEF_CRACKER.get()) ? this.getEffect(CAMobEffects.REEF_CRACKER.get()).getAmplifier() : -1;
			int nextAmplifier = amplifier < 0 ? 0 : Math.min(amplifier + 1, 11);
			int duration = amplifier >= 0 && amplifier < 11 ? 120 : 80;
			this.addEffect(new MobEffectInstance(CAMobEffects.REEF_CRACKER.get(), duration, nextAmplifier, false, false));
		}
		return damaged;
	}

	@Override
	public void setHealth(float pHealth) {
		if (pHealth <= 0.0F && this.canTransitionToNextPhase()) {
			super.setHealth(1.0F);
			if (this.bossInfo != null) {
				float maxHealth = this.getMaxHealth();
				this.bossInfo.setProgress(maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F));
			}
			this.startNextPhaseTransition();
			return;
		}
		super.setHealth(pHealth);
		if (this.bossInfo != null) {
			float maxHealth = this.getMaxHealth();
			this.bossInfo.setProgress(maxHealth <= 0.0F ? 0.0F : Mth.clamp(this.getHealth() / maxHealth, 0.0F, 1.0F));
		}
	}

	@Override
	public void die(DamageSource source) {
		if (this.canTransitionToNextPhase()) {
			this.startNextPhaseTransition();
			return;
		}
		super.die(source);
		if (!this.hasNextPhase()) {
			LevelAccessor world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			MapVariablesHandler.resetAllEndspeakerAbilities(world);
			if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
				if (world.getBlockState(BlockPos.containing(x, y, z)).canBeReplaced()) {
					world.setBlock(BlockPos.containing(x, y, z), CABlocks.ENDSPEAKER_NEST.get().defaultBlockState(), 3);
				} else if (world.getBlockState(BlockPos.containing(x, y + 1, z)).canBeReplaced()) {
					world.setBlock(BlockPos.containing(x, y + 1, z), CABlocks.ENDSPEAKER_NEST.get().defaultBlockState(), 3);
				}
			}
			for (Entity entity : new ArrayList<>(world.players())) {
				if (this.level().dimension() != entity.level().dimension() || this.distanceTo(entity) >= 64) {
					continue;
				}
				if (entity instanceof ServerPlayer player) {
					Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "silent_interruption"));
					AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
					if (!progress.isDone()) {
						for (String criteria : progress.getRemainingCriteria()) {
							player.getAdvancements().award(advancement, criteria);
						}
					}
				}
			}
		}
	}

	protected void startNextPhaseTransition() {
		this.setAnimation("animation.endspeaker_" + this.getPhase() + ".die");
		switch (this.getPhase()) {
			case 0, 1 -> this.setEvolveTime(300);
			case 2 -> {
				this.setEvolveTime(300);
				this.setDuration(999);
			}
			default -> {
			}
		}
	}

	protected void transitionToNextPhase(int nextPhase) {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity sacrifice;
		double targetX;
		double targetY;
		double targetZ;
		double radius;
		Entity result;
		Entity edibleTarget = null;
		Entity nearestPlayer = null;
		double minDist = 999;
		double minPlayerDist = 999;
		double bestowed = 0;
		{
			final Vec3 center = new Vec3(x, y + 24, z);
			List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), candidate -> true).stream()
					.sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
					.toList();
			for (Entity candidate : nearbyEntities) {
				if (!(candidate instanceof Monster || candidate instanceof Player)) {
					continue;
				}
				if (candidate.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_edible")))) {
					if (candidate instanceof BaselayerAbyssalEntity && hasAbility(world, 0)) {
						continue;
					} else if (candidate instanceof PredatorAbyssalEntity && hasAbility(world, 1)) {
						continue;
					} else if (candidate instanceof GuideAbyssalEntity && hasAbility(world, 2)) {
						continue;
					} else if (candidate instanceof SplasherAbyssalEntity && hasAbility(world, 3)) {
						continue;
					} else if (candidate instanceof UmbrellaAbyssalEntity && hasAbility(world, 4)) {
						continue;
					} else if (candidate instanceof CrackerAbyssalEntity && hasAbility(world, 5)) {
						continue;
					}
					double currentDistance = this.distanceTo(candidate);
					if (currentDistance < minDist) {
						minDist = currentDistance;
						edibleTarget = candidate;
					}
				} else if (candidate instanceof Player player && !player.isCreative() && !player.isSpectator()) {
					double currentPlayerDistance = this.distanceTo(candidate);
					if (currentPlayerDistance < minPlayerDist) {
						minPlayerDist = currentPlayerDistance;
						nearestPlayer = player;
					}
				}
			}
		}
		if (edibleTarget == null) {
			result = nearestPlayer == null ? this : nearestPlayer;
		} else {
			if (edibleTarget instanceof BaselayerAbyssalEntity) {
				bestowed = 0;
			} else if (edibleTarget instanceof PredatorAbyssalEntity) {
				bestowed = 1;
			} else if (edibleTarget instanceof GuideAbyssalEntity) {
				bestowed = 2;
			} else if (edibleTarget instanceof SplasherAbyssalEntity) {
				bestowed = 3;
			} else if (edibleTarget instanceof UmbrellaAbyssalEntity) {
				bestowed = 4;
			} else if (edibleTarget instanceof CrackerAbyssalEntity) {
				bestowed = 5;
			}
			MapVariablesHandler.bestowAbility(world, bestowed);
			result = edibleTarget;
		}
		sacrifice = result;
        targetX = sacrifice.getX();
        targetY = sacrifice.getY();
        targetZ = sacrifice.getZ();
        radius = 1 + nextPhase;
        if (world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), CASounds.ENDSPEAKER_EAT.get(), SoundSource.HOSTILE, 4, 1);
        }
        final Vec3 center = new Vec3(targetX, targetY, targetZ);
        List<Entity> affectedEntities = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate((2 * radius) / 2d), candidate -> true).stream()
                .sorted(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(center)))
                .toList();
        for (Entity candidate : affectedEntities) {
            if (!(candidate instanceof LivingEntity)) {
                continue;
            }
            if (candidate.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffpsring"))) && candidate instanceof Player) {
                continue;
            }
            if ((this.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization > 2) {
                continue;
            }
            if (sacrifice.distanceTo(candidate) < radius) {
                candidate.hurt(CADamageTypes.source(world, CADamageTypes.OCEANKILLER_DAMAGE),
                        (float) (CAConfigs.SANITY_BREAK.get() * 6));
            }
        }
        if (!(sacrifice instanceof Player)) {
            if (sacrifice.isAlive()) {
                sacrifice.hurt(CADamageTypes.source(world, CADamageTypes.OCEANKILLER_DAMAGE), 114514);
            }
            if (sacrifice.isAlive() && !sacrifice.level().isClientSide()) {
                sacrifice.discard();
            }
        }
        if (world instanceof ServerLevel level) {
			level.sendParticles(CAParticles.ENDSPEAKER_PARTICLE.get(), targetX, targetY + 1, targetZ, 128, 1, 1, 1, 0.075);
			EndspeakerEntity.spawnForPhase(level, BlockPos.containing(targetX, targetY, targetZ), MobSpawnType.MOB_SUMMONED, nextPhase);
		}
	}

	protected void updatePrefixName() {
		StringBuilder prefixes = new StringBuilder();
		int count = 0;
		String[] prefixArray = Component.translatable("entity.caerula_arbor.endspeaker.prefix").getString().split(",");
		for (int index = 0; index < prefixArray.length; index++) {
			if (this.hasAbility(index)) {
				prefixes.append(prefixArray[index]);
				count++;
			}
		}
		if (count > 5) {
			prefixes = new StringBuilder(Component.translatable("entity.caerula_arbor.endspeaker.prefix.all").getString());
		}
		if (count >= 4) {
			prefixes.insert(0, "§b");
		} else if (count >= 2) {
			prefixes.insert(0, "§e");
		}
		if (!prefixes.isEmpty()) {
			this.setCustomName(Component.literal(prefixes + this.getDisplayName().getString()));
		}
	}

	protected void spawnSeabornWave(double eliteChance, int spawnCount) {
		LevelAccessor world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		double spawnX;
		double spawnY;
		double spawnZ;
		if (!MapVariables.get(world).endspeakerSummon) {
			return;
		}
		if (EntityUtils.getSeabornNum(world, x, y, z) >= world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)) {
			return;
		}
		for (int attempt = 0; attempt < 8; attempt++) {
			spawnX = x + Mth.nextInt(RandomSource.create(), -8, 8);
			spawnZ = z + Mth.nextInt(RandomSource.create(), -8, 8);
			spawnY = WorldUtils.findValidSpawnY(world, x, y, z, spawnX, y, spawnZ);
			if (Double.isNaN(spawnY)) {
				continue;
			}
			int randomType = Mth.nextInt(RandomSource.create(), 0, 5);
			if (world instanceof ServerLevel level) {
				Entity entityToSpawn = switch (randomType) {
					case 1 -> CAEntities.PREDATOR_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
					case 2 -> CAEntities.GUIDE_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
					case 3 -> CAEntities.SPLASHER_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
					case 4 -> CAEntities.UMBRELLA_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
					case 5 -> CAEntities.CRACKER_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
					default -> CAEntities.BASELAYER_ABYSSAL.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
				};
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
				level.sendParticles(ParticleTypes.CLOUD, spawnX, spawnY + 0.75, spawnZ, 64, 0.75, 0.75, 0.75, 0.1);
			}
			break;
		}
		for (int spawnIndex = 0; spawnIndex < spawnCount - 1; spawnIndex++) {
			for (int attempt = 0; attempt < 8; attempt++) {
				spawnX = x + Mth.nextInt(RandomSource.create(), -8, 8);
				spawnZ = z + Mth.nextInt(RandomSource.create(), -8, 8);
				spawnY = WorldUtils.findValidSpawnY(world, x, y, z, spawnX, y, spawnZ);
				if (Double.isNaN(spawnY)) {
					continue;
				}
				SeabornSpawnManager.summonRandomSeaborn(world, eliteChance, spawnX, spawnY, spawnZ);
				if (world instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.CLOUD, spawnX, spawnY + 0.75, spawnZ, 64, 0.75, 0.75, 0.75, 0.1);
				}
				break;
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Phase", this.getPhase());
		if (this.getPhase() == 0 || this.getPhase() == 1 || this.getPhase() == 2) {
			compound.putBoolean("IsEvolving", this.entityData.get(DATA_IS_EVOLVING));
			compound.putInt("EvolveTime", this.getEvolveTime());
		}
		if (this.getPhase() == 2) {
			compound.putInt("Duration", this.getDuration());
			compound.putInt("SkillCooldown", this.getSkillCooldown());
		} else if (!this.hasNextPhase()) {
			compound.putInt("Duration", this.getDuration());
			compound.putInt("SkillCooldown", this.getSkillCooldown());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Phase")) {
			this.setPhase(compound.getInt("Phase"));
		}
		if (this.getPhase() == 0 || this.getPhase() == 1 || this.getPhase() == 2) {
			if (compound.contains("IsEvolving")) {
			    this.entityData.set(DATA_IS_EVOLVING, compound.getBoolean("IsEvolving"));
			}
			if (compound.contains("EvolveTime")) {
				this.setEvolveTime(compound.getInt("EvolveTime"));
			}
		}
		if (this.getPhase() == 2) {
			if (compound.contains("Duration")) {
				this.setDuration(compound.getInt("Duration"));
			}
			if (compound.contains("SkillCooldown")) {
				this.setSkillCooldown(compound.getInt("SkillCooldown"));
			}
		} else if (!this.hasNextPhase()) {
			if (compound.contains("Duration")) {
				this.setDuration(compound.getInt("Duration"));
			}
			if (compound.contains("SkillCooldown")) {
				this.setSkillCooldown(compound.getInt("SkillCooldown"));
			}
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData spawnData = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		if (this.getPhase() == 0) {
			this.setAnimation("animation.endspeaker_0.start");
		} else if (this.getPhase() == 1) {
			this.setAnimation("animation.endspeaker_1.start");
			if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())) {
				this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
			}
			if (this.getAttributes().hasAttribute(CAAttributes.SANITY_RATE.get())) {
				this.getAttribute(CAAttributes.SANITY_RATE.get()).setBaseValue(60);
			}
			if (!this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 50, 9, false, false));
			}
			this.applySpawnAbilities(world);
			this.updatePrefixName();
		} else if (this.getPhase() == 2) {
			this.setAnimation("animation.endspeaker_2.start");
			this.setSkillCooldown(100);
			if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())) {
				this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
			}
			if (!this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 68, 9, false, false));
			}
			this.applySpawnAbilities(world);
			this.updatePrefixName();
		} else if (!this.hasNextPhase()) {
			this.setAnimation("animation.endspeaker_3.start");
			this.setSkillCooldown(200);
			if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())) {
				this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
			}
			if (!this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 50, 9, false, false));
			}
			this.applySpawnAbilities(world);
			this.updatePrefixName();
		}
		return spawnData;
	}

	private void applySpawnAbilities(LevelAccessor world) {
		if (hasAbility(world, 0) && this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())) {
			this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(this.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue() + 40);
		}
		if (hasAbility(world, 1) && this.getAttributes().hasAttribute(CAAttributes.MISSRATE.get())) {
			this.getAttribute(CAAttributes.MISSRATE.get()).setBaseValue(this.getAttribute(CAAttributes.MISSRATE.get()).getBaseValue() + 50);
		}
	}

	private void tickPhaseZeroBehavior() {
		if (!this.isAlive()) {
			return;
		}
		double evolveTime = this.getEvolveTime();
		if (evolveTime > 0) {
			this.entityData.set(DATA_IS_EVOLVING, true);
			this.setHealth((float) Math.max(Math.round(this.getMaxHealth() * (300 - evolveTime) * 0.0033333), 1));
			if (!this.hasEffect(CAMobEffects.INVULNERABLE.get())) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 300, 1, false, false));
			}
			if (evolveTime <= 260 && !this.hasEffect(MobEffects.INVISIBILITY)) {
				this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
			}
			if (evolveTime == 1) {
				this.transitionToNextPhase(1);
				if (!this.level().isClientSide) {
					this.discard();
				}
			}
			this.setEvolveTime((int) (evolveTime - 1));
		} else {
			this.entityData.set(DATA_IS_EVOLVING, false);
		}
		double spawnGap = this.entityData.get(DATA_IS_EVOLVING) ? 100 : 600;
		if (this.tickCount % spawnGap == 33 && EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) < 32) {
			this.spawnSeabornWave(0.05, 3);
		}
	}

	private void tickBestowedAbilities() {
		if (this.tickCount % 5 != 0) {
			return;
		}
		if (this.hasAbility(2) && this.getHealth() < this.getMaxHealth() * 0.4F) {
			if (!this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 0, false, false));
				this.addEffect(new MobEffectInstance(CAMobEffects.ENDSPEAER_BRANDGUIDE_BUFF.get(), 25, 0));
			}
		}
		if (this.hasAbility(4)) {
			this.clearFire();
			this.removeEffect(CAMobEffects.DIZZY.get());
			this.removeEffect(CAMobEffects.MUTE.get());
			this.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
			this.removeEffect(MobEffects.WEAKNESS);
			this.removeEffect(CAMobEffects.FROZEN.get());
			this.setTicksFrozen(0);
			if (this.tickCount % 200 == 0 && !this.hasEffect(CAMobEffects.ESSENCE_RESISTANCE.get()) && !this.level().isClientSide()) {
				this.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 180, 2, false, false));
			}
			double movementSpeed = this.getAttributeValue(Attributes.MOVEMENT_SPEED);
			if (EntityUtils.getSpeed(this) > movementSpeed * 1.25D) {
				this.setDeltaMovement(Vec3.ZERO);
			}
		}
		if (this.hasAbility(1)) {
			double missRate = 50.0D;
			if (this.isOnFire() && !this.fireImmune()) {
				missRate = 0.0D;
			}
			if (this.hasEffect(CAMobEffects.DIZZY.get())
				|| this.hasEffect(CAMobEffects.FROZEN.get())
				|| this.hasEffect(MobEffects.LEVITATION)
				|| this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
				|| this.hasEffect(MobEffects.SLOW_FALLING)) {
				missRate = 0.0D;
			}
			if (this.getAttributes().hasAttribute(CAAttributes.MISSRATE.get())) {
				this.getAttribute(CAAttributes.MISSRATE.get()).setBaseValue(missRate);
			}
		}
	}

	private void tickPhaseOneBehavior() {
		if (!this.isAlive() || this.tickCount < 75) {
			return;
		}
		double evolveTime = this.getEvolveTime();
		if (evolveTime > 0) {
			this.entityData.set(DATA_IS_EVOLVING, true);
			this.setHealth((float) Math.max(Math.round(this.getMaxHealth() * (300 - evolveTime) * 0.0033333), 1));
			if (!this.hasEffect(CAMobEffects.INVULNERABLE.get())) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 300, 1, false, false));
			}
			if (evolveTime <= 260 && !this.hasEffect(MobEffects.INVISIBILITY)) {
				this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
			}
			if (evolveTime == 1) {
				this.transitionToNextPhase(2);
				if (!this.level().isClientSide) {
					this.discard();
				}
			}
			this.setEvolveTime((int) (evolveTime - 1));
		} else {
			this.entityData.set(DATA_IS_EVOLVING, false);
		}
		double spawnGap = this.entityData.get(DATA_IS_EVOLVING) ? 100 : 600;
		if (this.tickCount % spawnGap == 50 && EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) < 32) {
			this.spawnSeabornWave(0.085, 4);
		}
		this.tickBestowedAbilities();
	}

	private void tickPhaseTwoBehavior() {
		if (!this.isAlive() || this.tickCount < 35) {
			return;
		}
		double evolveTime = this.getEvolveTime();
		if (evolveTime > 0) {
			this.entityData.set(DATA_IS_EVOLVING, true);
			this.setHealth((float) Math.max(Math.round(this.getMaxHealth() * (300 - evolveTime) * 0.0033333), 1));
			if (!this.hasEffect(CAMobEffects.INVULNERABLE.get())) {
				this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 300, 1, false, false));
			}
			if (evolveTime <= 260 && !this.hasEffect(MobEffects.INVISIBILITY)) {
				this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
			}
			if (evolveTime == 1) {
				this.transitionToNextPhase(3);
				if (!this.level().isClientSide) {
					this.discard();
				}
			}
			this.setEvolveTime((int) (evolveTime - 1));
		} else {
			this.entityData.set(DATA_IS_EVOLVING, false);
		}
		double spawnGap = this.entityData.get(DATA_IS_EVOLVING) ? 80 : 400;
		if (this.tickCount % spawnGap == 33 && EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) < 32) {
			this.spawnSeabornWave(0.1, 5);
		}
		this.tickBestowedAbilities();
		Entity target = this.getTarget();
		if (this.getDuration() > 0) {
			this.setDuration(this.getDuration() - 1);
		}
		if (this.entityData.get(DATA_IS_EVOLVING)) {
			this.setSkillCooldown(10000);
			return;
		}
		if (this.getSkillCooldown() > 0) {
			this.setSkillCooldown(this.getSkillCooldown() - 1);
			return;
		}
		if (target != null && target.isAlive() && this.distanceTo(target) <= 4) {
			this.setAnimation("animation.endspeaker_2.skill");
			this.setDuration(36);
			this.setSkillCooldown(170);
			CaerulaArborMod.queueServerWork(9, () -> this.executePhaseTwoSkillWave(8, 4, 1.75));
			CaerulaArborMod.queueServerWork(19, () -> this.executePhaseTwoSkillWave(10, 9, 2.25));
		}
	}

	private void tickPhaseThreeBehavior() {
		if (!this.isAlive() || this.tickCount < 35) {
			return;
		}
		if (this.tickCount % 400 == 33 && EntityUtils.getSeabornNum(this.level(), this.getX(), this.getY(), this.getZ()) < 32) {
			this.spawnSeabornWave(0.15, 5);
		}
		this.tickBestowedAbilities();
		Entity target = this.getTarget();
		if (this.getDuration() > 0) {
			this.setDuration(this.getDuration() - 1);
		}
		if (this.getSkillCooldown() > 0) {
			this.setSkillCooldown(this.getSkillCooldown() - 1);
			return;
		}
		if (target != null && target.isAlive() && this.distanceTo(target) <= 24) {
			this.setAnimation("animation.endspeaker_3.skill");
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 50, 9, false, false));
			this.setSkillCooldown(280);
			this.setDuration(60);
			CaerulaArborMod.queueServerWork(16, this::teleportToTarget);
			CaerulaArborMod.queueServerWork(19, () -> this.executePhaseThreeChop(this.getX(), this.getY(), this.getZ(), false));
			CaerulaArborMod.queueServerWork(21, this::teleportToTarget);
			CaerulaArborMod.queueServerWork(24, () -> this.executePhaseThreeChop(this.getX(), this.getY(), this.getZ(), false));
			CaerulaArborMod.queueServerWork(26, this::teleportToTarget);
			CaerulaArborMod.queueServerWork(30, () -> this.executePhaseThreeChop(this.getX(), this.getY(), this.getZ(), true));
			CaerulaArborMod.queueServerWork(34, this::teleportToTarget);
			CaerulaArborMod.queueServerWork(37, () -> this.executePhaseThreeChop(this.getX(), this.getY(), this.getZ(), true));
			CaerulaArborMod.queueServerWork(41, () -> this.teleportTo(this.getX(), this.getY(), this.getZ()));
			CaerulaArborMod.queueServerWork(44, () -> this.executePhaseThreeChop(this.getX(), this.getY(), this.getZ(), true));
		}
	}

	private void teleportToTarget() {
		Entity target = this.getTarget();
		if (target != null && target.isAlive()) {
			this.teleportTo(target.getX(), target.getY(), target.getZ());
		}
	}

	private void executePhaseThreeChop(double x, double y, double z, boolean ranged) {
		Entity target = this.getTarget();
		double attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
		boolean canAttack = target != null && target.isAlive();
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), CASounds.ENDSPEAKER_ATTACK_HIT.get(), SoundSource.HOSTILE, 3.5F, 1);
		} else {
			this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), CASounds.ENDSPEAKER_ATTACK_HIT.get(), SoundSource.HOSTILE, 3.5F, 1, false);
		}
		if (ranged) {
			this.executePhaseThreeRangedChop(x, y, z, attackDamage, target);
		} else if (canAttack && target instanceof LivingEntity livingTarget) {
			float healthBeforeHit = livingTarget.getHealth();
			float absorptionBeforeHit = livingTarget.getAbsorptionAmount();
			if (this.hurtWithEndspeakerAttack(livingTarget, (float) (attackDamage * 1.5))) {
				this.handlePhaseThreeAttackHit(livingTarget, healthBeforeHit, absorptionBeforeHit);
			}
		}
	}

	private void executePhaseThreeRangedChop(double x, double y, double z, double attackDamage, Entity target) {
		Vec3 center = new Vec3(x, y, z);
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(8 / 2d), entity -> true).stream()
			.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center))).toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (!(nearbyEntity instanceof LivingEntity livingTarget)) {
				continue;
			}
			if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				if (nearbyEntity != target) {
					continue;
				}
			}
			if (nearbyEntity == this) {
				continue;
			}
			if (this.distanceTo(nearbyEntity) <= 4) {
				float healthBeforeHit = livingTarget.getHealth();
				float absorptionBeforeHit = livingTarget.getAbsorptionAmount();
				if (this.hurtWithEndspeakerAttack(livingTarget, (float) (attackDamage * 0.9))) {
					this.handlePhaseThreeAttackHit(livingTarget, healthBeforeHit, absorptionBeforeHit);
				}
			}
		}
	}

	private void handlePhaseThreeAttackHit(LivingEntity target, float healthBeforeHit, float absorptionBeforeHit) {
		float dealtDamage = Math.max(0.0F, healthBeforeHit + absorptionBeforeHit - target.getHealth() - target.getAbsorptionAmount());
		if (dealtDamage <= 0.0F) {
			return;
		}
		if (this.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(CAParticles.ENDSPEAKER_PARTICLE.get(), target.getX(), target.getY() + 0.75, target.getZ(), 8, 0.75, 0.75, 0.75, 0.1);
		}
		if (this.getHealth() >= this.getMaxHealth() * 0.4F) {
			this.setHealth(this.getHealth() + dealtDamage * 1.4F);
		} else {
			this.setHealth(this.getHealth() + dealtDamage * 1.8F);
		}
	}

	private void executePhaseTwoSkillWave(double radius, double maxTargets, double damageMultiplier) {
		if (!this.isAlive()) {
			return;
		}
		Entity target = this.getTarget();
		double remainingTargets = maxTargets;
		double attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
		if (target instanceof LivingEntity livingTarget && target.isAlive()) {
			if (radius <= 8) {
				target.push(0, 0.33, 0);
			}
			this.hurtWithEndspeakerAttack(livingTarget, (float) (attackDamage * damageMultiplier));
			remainingTargets -= 1;
		}
		Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(radius / 2d), entity -> true).stream()
			.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center))).toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (!(nearbyEntity instanceof LivingEntity)) {
				continue;
			}
			if (nearbyEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				if (nearbyEntity != target) {
					continue;
				}
			}
			if (nearbyEntity == this) {
				continue;
			}
			if (this.distanceTo(nearbyEntity) <= radius / 2d) {
				if (radius <= 8) {
					nearbyEntity.push(0, 0.33, 0);
				}
				this.hurtWithEndspeakerAttack((LivingEntity) nearbyEntity, (float) (attackDamage * damageMultiplier));
				remainingTargets -= 1;
			}
			if (remainingTargets <= 0) {
				break;
			}
		}
	}

	protected PlayState phaseZeroMovementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_0.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_0.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_0.idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState phaseOneMovementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_1.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_1.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_1.idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState phaseOneAttackingPredicate(AnimationState<?> event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_1.attack"));
		}
		return PlayState.CONTINUE;
	}

	protected PlayState phaseTwoMovementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_2.die"));
			}
			if (this.isAggressive() && event.isMoving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_2.sprint"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_2.idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState phaseTwoAttackingPredicate(AnimationState<?> event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 7L <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_2.attack"));
		}
		return PlayState.CONTINUE;
	}

	protected PlayState phaseThreeMovementPredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			if (event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_3.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_3.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.endspeaker_3.idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState phaseThreeAttackingPredicate(AnimationState<?> event) {
		if (this.getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 30L <= this.level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.endspeaker_3.attack"));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		if (this.getPhase() == 0) {
			data.add(new AnimationController<>(this, "movement", 0, this::phaseZeroMovementPredicate));
			data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
		} else if (this.getPhase() == 1) {
			data.add(new AnimationController<>(this, "movement", 0, this::phaseOneMovementPredicate));
			data.add(new AnimationController<>(this, "attacking", 0, this::phaseOneAttackingPredicate));
			data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
		} else if (this.getPhase() == 2) {
			data.add(new AnimationController<>(this, "movement", 0, this::phaseTwoMovementPredicate));
			data.add(new AnimationController<>(this, "attacking", 0, this::phaseTwoAttackingPredicate));
			data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
		} else if (!this.hasNextPhase()) {
			data.add(new AnimationController<>(this, "movement", 0, this::phaseThreeMovementPredicate));
			data.add(new AnimationController<>(this, "attacking", 0, this::phaseThreeAttackingPredicate));
			data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
		}
	}

	protected PlayState procedurePredicate(AnimationState<?> event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED
			|| (!this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(this.prevAnim)) {
				event.getController().forceAnimationReset();
			}
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			this.prevAnim = "empty";
			return PlayState.STOP;
		}
		this.prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.getPhase() == 0) {
			this.tickPhaseZeroBehavior();
		} else if (this.getPhase() == 1) {
			this.tickPhaseOneBehavior();
		} else if (this.getPhase() == 2) {
			this.tickPhaseTwoBehavior();
		} else if (!this.hasNextPhase()) {
			this.tickPhaseThreeBehavior();
		}
		this.updatePhaseRuntimeProperties();
		this.refreshDimensions();
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (!this.level().isClientSide() && this.deathTime == this.getPhaseDeathTickThreshold() && !this.isRemoved()) {
			this.remove(Mob.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	//TODO 或许可以修改为每级+5
	protected int getPhaseDeathTickThreshold() {
		return switch (this.getPhase()) {
			case 1 -> 40;
			case 2 -> 45;
			case 3 -> 50;
			default -> 35;
		};
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
