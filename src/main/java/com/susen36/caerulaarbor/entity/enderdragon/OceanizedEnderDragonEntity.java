package com.susen36.caerulaarbor.entity.enderdragon;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.ServerGeoAnimator;
import com.susen36.caerulaarbor.client.model.entity.OceanizedEnderDragonModel;
import com.susen36.caerulaarbor.entity.MoistDragonBreathEntity;
import com.susen36.caerulaarbor.entity.MoistEnderCrystalEntity;
import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.*;

public class OceanizedEnderDragonEntity extends SeaMonster implements RangedAttackMob {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_REVIVE_TICK = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SKILL_P = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(OceanizedEnderDragonEntity.class, EntityDataSerializers.INT);
	public static SoundEvent PRE = CASounds.CASTER_PRE.get();
	private boolean swinging;
	private long lastSwing;
	public String animationprocedure = "empty";
	public Set<String> crystals = new HashSet<>();
	private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.PINK, ServerBossEvent.BossBarOverlay.NOTCHED_10);

	public final double[][] positions = new double[64][3];
	public int posPointer = -1;
	public float flapTime;
	public float yRotA;
	private final OceanizedEnderDragonPart[] subEntities;
	public final OceanizedEnderDragonPart head;
	private final OceanizedEnderDragonPart neck1;
	private final OceanizedEnderDragonPart neck2;
	private final OceanizedEnderDragonPart body;
	private final OceanizedEnderDragonPart tail1;
	private final OceanizedEnderDragonPart tail2;
	private final OceanizedEnderDragonPart tail3;
	private final OceanizedEnderDragonPart tail4;
	public final OceanizedEnderDragonPart wing1;
	private final OceanizedEnderDragonPart wing2;

	// 服务端动画泛型 Helper（内部含 model/processor/bakedModel/animTick/lastUpdateTime 5 个字段），
	// 仅保留 1 个 final 字段，后续 Hydra/Leviathan 等 SeaMonster 可直接 new ServerGeoAnimator 复用
	private final ServerGeoAnimator<OceanizedEnderDragonEntity> serverGeoAnimator;

	public OceanizedEnderDragonEntity(Level world) {
		this(CAEntities.OCEANIZED_ENDER_DRAGON.get(), world);
	}

	public OceanizedEnderDragonEntity(EntityType<OceanizedEnderDragonEntity> type, Level world) {
		super(type, world);
		this.serverGeoAnimator = new ServerGeoAnimator<>(this, new OceanizedEnderDragonModel());
		this.head = new OceanizedEnderDragonPart(this, "head", 1.25F, 1.25F);
		this.neck1 = new OceanizedEnderDragonPart(this, "neck2", 1.75F, 1.75F);
		this.neck2 = new OceanizedEnderDragonPart(this, "neck4", 1.75F, 1.75F);
		this.body = new OceanizedEnderDragonPart(this, "body", 5.0F, 3.0F);
		this.tail1 = new OceanizedEnderDragonPart(this, "tail2", 1.75F, 1.75F);
		this.tail2 = new OceanizedEnderDragonPart(this, "tail5", 1.75F, 1.75F);
		this.tail3 = new OceanizedEnderDragonPart(this, "tail8", 1.75F, 1.75F);
		this.tail4 = new OceanizedEnderDragonPart(this, "tail11", 1.75F, 1.75F);
		this.wing1 = new OceanizedEnderDragonPart(this, "left_wing_tip", 4.0F, 1.75F);
		this.wing2 = new OceanizedEnderDragonPart(this, "right_wing_tip", 4.0F, 1.75F);
		this.subEntities = new OceanizedEnderDragonPart[]{this.head, this.neck1, this.neck2, this.body, this.tail1, this.tail2, this.tail3, this.tail4, this.wing1, this.wing2};
		this.noPhysics = true;
		this.noCulling = true;
		xpReward = 128;
		setNoAi(false);
		setNoGravity(true);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6f);
		setPersistenceRequired();
		this.moveControl = new FlyingMoveControl(this, 10, true);
		this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_SHOOT, false);
		builder.define(DATA_ANIMATION, "undefined");
		builder.define(DATA_REVIVE_TICK, 0);
		builder.define(DATA_PHASE, 0);
		builder.define(DATA_SKILL_P, 0);
		builder.define(DATA_DURATION, 50);
	}

	public float getHeadPartYOffset(int index, double[] basePosition, double[] currentPosition) {
		if (this.isShiftKeyDown()) {
			return index;
		}
		if (index == 6) {
			return 0.0F;
		}
		return (float) (currentPosition[1] - basePosition[1]);
	}

	private float getHeadYOffset() {
		if (!this.isReviving()) {
			return -1.0F;
		} else {
			double[] adouble = this.getLatencyPos(5, 1.0F);
			double[] adouble1 = this.getLatencyPos(0, 1.0F);
			return (float)(adouble[1] - adouble1[1]);
		}
	}

	public double[] getLatencyPos(int index, float partialTick) {
		if (this.isDeadOrDying()) {
			partialTick = 0.0F;
		}
		partialTick = 1.0F - partialTick;
		int currentIndex = this.posPointer - index & 63;
		int previousIndex = this.posPointer - index - 1 & 63;
		double[] result = new double[3];
		double currentYaw = this.positions[currentIndex][0];
		double yawDelta = Mth.wrapDegrees(this.positions[previousIndex][0] - currentYaw);
		result[0] = currentYaw + yawDelta * (double) partialTick;
		double currentY = this.positions[currentIndex][1];
		result[1] = currentY + (this.positions[previousIndex][1] - currentY) * (double) partialTick;
		result[2] = Mth.lerp(partialTick, this.positions[currentIndex][2], this.positions[previousIndex][2]);
		return result;
	}

	@Override
	protected PathNavigation createNavigation(Level pLevel) {
		FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(true);
		return flyingpathnavigation;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new DoNothingGoal());
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 60, 15.0F));
		this.goalSelector.addGoal(5, new DragonWanderGoal(this, 1.25D));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 32.0F));
	}

	public void putCrystal(Entity crystal) {
		this.crystals.add(crystal.getStringUUID());
	}

	public MoistEnderCrystalEntity getCrystal(String uuid) {
		Level level = this.level();
		if (level instanceof ServerLevel serverLevel && uuid != null && !uuid.isEmpty()) {
			Entity entity = serverLevel.getEntity(UUID.fromString(uuid));
			return entity instanceof MoistEnderCrystalEntity ? (MoistEnderCrystalEntity) entity : null;
		}
		return null;
	}

	public class RangedAttackGoal extends Goal {
		private final Mob mob;
		private final RangedAttackMob rangedAttackMob;
		@Nullable
		private LivingEntity target;
		private int attackTime = -1;
		private final double speedModifier;
		private int seeTime;
		private final int attackIntervalMin;
		private final int attackIntervalMax;
		private final float attackRadius;
		private final float attackRadiusSqr;
		private Vec3 lastPathTarget;
		private int pathRecalcCooldown;

		public RangedAttackGoal(RangedAttackMob p_25768_, double p_25769_, int p_25770_, float p_25771_) {
			this(p_25768_, p_25769_, p_25770_, p_25770_, p_25771_);
		}

		public RangedAttackGoal(RangedAttackMob p_25773_, double p_25774_, int p_25775_, int p_25776_, float p_25777_) {
			if (!(p_25773_ instanceof LivingEntity)) {
				throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
			} else {
				this.rangedAttackMob = p_25773_;
				this.mob = (Mob) p_25773_;
				this.speedModifier = p_25774_;
				this.attackIntervalMin = p_25775_;
				this.attackIntervalMax = p_25776_;
				this.attackRadius = p_25777_;
				this.attackRadiusSqr = p_25777_ * p_25777_;
				this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			}
		}

		@Override
		public boolean canUse() {
			LivingEntity livingentity = this.mob.getTarget();
			if (livingentity != null && livingentity.isAlive()) {
				this.target = livingentity;
				return isEnderinaDurative();
			} else {
				return false;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
		}

		@Override
		public void start() {
			this.pathRecalcCooldown = 0;
			this.recalculatePath();
		}

		@Override
		public void stop() {
			this.target = null;
			this.seeTime = 0;
			this.attackTime = -1;
			this.pathRecalcCooldown = 0;
			((OceanizedEnderDragonEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}

		private void recalculatePath() {
			this.mob.getNavigation().moveTo(this.target, this.speedModifier);
			this.lastPathTarget = this.target.position();
			this.pathRecalcCooldown = 10;
		}

		@Override
		public void tick() {
			double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
			if (flag) {
				++this.seeTime;
			} else {
				this.seeTime = 0;
			}
			if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
				this.mob.getNavigation().stop();
			} else {
				if (this.pathRecalcCooldown > 0) {
					--this.pathRecalcCooldown;
				}
				double dx = this.target.getX() - this.lastPathTarget.x;
				double dy = this.target.getY() - this.lastPathTarget.y;
				double dz = this.target.getZ() - this.lastPathTarget.z;
				if (this.pathRecalcCooldown == 0 && (this.mob.getNavigation().isDone() || this.mob.horizontalCollision || this.mob.verticalCollision
					|| Mth.square(dx) + Mth.square(dy) + Mth.square(dz) > 16.0D)) {
					this.recalculatePath();
				}
			}
			this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (--this.attackTime == 0) {
				if (!flag) {
					((OceanizedEnderDragonEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
					return;
				}
				((OceanizedEnderDragonEntity) rangedAttackMob).entityData.set(DATA_SHOOT, true);
				float f = (float) Math.sqrt(d0) / this.attackRadius;
				float f1 = Mth.clamp(f, 0.1F, 1.0F);
				this.rangedAttackMob.performRangedAttack(this.target, f1);
				this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
			} else if (this.attackTime < 0) {
				this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
			} else
				((OceanizedEnderDragonEntity) rangedAttackMob).entityData.set(DATA_SHOOT, false);
		}
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for(int i = 0; i < this.subEntities.length; ++i) {
			this.subEntities[i].setId(id + i + 1);
		}
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	public PartEntity<?> [] getParts() {
		return this.subEntities;
	}

	@Override
	public void recreateFromPacket(ClientboundAddEntityPacket packet) {
		super.recreateFromPacket(packet);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return CASounds.CASTER_HURT.get();
	}

	@Override
	public SoundEvent getDeathSound() {
		return CASounds.CASTER_DIE.get();
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DRAGON_BREATH)) return false;
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypeTags.BYPASSES_EFFECTS) || source == this.damageSources().genericKill())
			return super.hurt(source, amount);
		float rate = isReviving() ? 0.25f : 1;
        float actualDamage = Math.min(amount * rate, this.getMaxHealth() * 0.33f);
		return super.hurt(source, actualDamage);
	}

	public boolean hurt(OceanizedEnderDragonPart part, DamageSource source, float amount) {
		return this.hurt(source, amount);
	}

    @Override
    public void die(DamageSource source) {
        if (this.getEntityData().get(DATA_PHASE) == 0 && !source.is(DamageTypes.GENERIC_KILL) && canEnterPhaseTwo(source)) {
            this.getEntityData().set(DATA_REVIVE_TICK, 200);
            this.getEntityData().set(DATA_PHASE, 1);
            if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 2);
            if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 3);
            if (this.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE))
                this.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(this.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue() * 2);
            if (this.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE))
                this.getAttribute(CAAttributes.MAGIC_RESISTANCE).setBaseValue(this.getAttribute(CAAttributes.MAGIC_RESISTANCE).getBaseValue() + 20);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
            }
            this.setHealth(this.getMaxHealth());
            return;
        }
        if (!this.isReviving()) {
            this.setHealth(this.getMaxHealth() * 0.5F);
            this.getEntityData().set(OceanizedEnderinaEntity.DATA_DURATION, 600);
            this.getEntityData().set(OceanizedEnderinaEntity.DATA_REVIVE_TICK, 600);
            return;
        }
        super.die(source);
    }

    private boolean canEnterPhaseTwo(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) return false;
        if (attacker instanceof LivingEntity living && living.getOffhandItem().is(CAItems.ENDERINA_SPAWNER.get())) return true;
        if (attacker.getType().is(EntityUtils.OCEAN_OFFSPRING)) return false;
        if (attacker instanceof TamableAnimal tamable && tamable.isTame()) return false;
        return !(attacker instanceof Player) && !attacker.getType().is(EntityUtils.HUMAN);
    }

	@Override
	public void setHealth(float pHealth) {
		if (pHealth <= 0 && !this.isReviving()) {
			super.setHealth(this.getMaxHealth() * 0.5F);
			this.getEntityData().set(OceanizedEnderDragonEntity.DATA_DURATION, 600);
			this.getEntityData().set(OceanizedEnderDragonEntity.DATA_REVIVE_TICK, 600);
			return;
		}
		super.setHealth(pHealth);
	}

	@Override
	public void remove(RemovalReason pReason) {
		if (pReason == RemovalReason.DISCARDED && this.level().getDifficulty() != Difficulty.PEACEFUL) return;
		else if (pReason == RemovalReason.KILLED && !this.isDeadOrDying()) return;
		super.remove(pReason);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 9, false, false));
		return super.finalizeSpawn(world, difficulty, reason, livingdata);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		ListTag crystalsTag = new ListTag();
		this.crystals.forEach(uuid -> crystalsTag.add(StringTag.valueOf(uuid)));

		compound.putInt("ReviveTick", this.entityData.get(DATA_REVIVE_TICK));
		compound.putInt("Phase", this.entityData.get(DATA_PHASE));
		compound.putInt("SkillP", this.entityData.get(DATA_SKILL_P));
		compound.putInt("Duration", this.entityData.get(DATA_DURATION));
		compound.put("crystals", crystalsTag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("ReviveTick")) {
		    this.entityData.set(DATA_REVIVE_TICK, compound.getInt("ReviveTick"));
		}
		if (compound.contains("Phase")) {
		    this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
		}
		if (compound.contains("SkillP")) {
		    this.entityData.set(DATA_SKILL_P, compound.getInt("SkillP"));
		}
		if (compound.contains("Duration")) {
		    this.entityData.set(DATA_DURATION, compound.getInt("Duration"));
		}
		if (compound.contains("crystals")) {
			ListTag crystalsTag = compound.getList("crystals", 8);
			for (int index = 0; index < crystalsTag.size(); index++) {
				this.crystals.add(crystalsTag.getString(index));
			}
		}
	}

	@Override
	public void baseTick() {
		super.baseTick();
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity target;
		double dura;
		double P;
		double sklp1;
		double rev;
		double deadTime;
		deadTime = this.deathTime;
		if (this.isAlive()) {
			sklp1 = (Entity) this instanceof OceanizedEnderDragonEntity datEntI ? datEntI.getEntityData().get(DATA_SKILL_P) : 0;
			dura = (Entity) this instanceof OceanizedEnderDragonEntity datEntI ? datEntI.getEntityData().get(DATA_DURATION) : 0;
			rev = (Entity) this instanceof OceanizedEnderDragonEntity datEntI ? datEntI.getEntityData().get(DATA_REVIVE_TICK) : 0;
			if (tickCount % 100 == 0) {
				if (y > world.getMaxBuildHeight() - 20 && WorldUtils.hasNoSolidGroundWithin20Below(world, x, y, z)) {
					push(0, (-0.35), 0);
				}
			}
			P = (Entity) this instanceof OceanizedEnderDragonEntity datEntI ? datEntI.getEntityData().get(DATA_PHASE) : 0;
			if (rev > 0) {
				if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_REVIVE_TICK, (int) (rev - 1));
				setShiftKeyDown(true);
				setDeltaMovement(new Vec3(0, 0, 0));
				if (tickCount % 10 == 0) {
					this.swallowNearbyCrystals();
				}
				if (this.getHealth() >= this.getMaxHealth()) {
					if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
				}
				if (Math.random() < 0.033) {
					MoistDragonBreathEntity.dragonBreathRain(world, x, y, z, this);
				}
				if (P > 0.5) {
					if (Math.random() < 0.033) {
						MoistDragonBreathEntity.dragonBreathRain(world, x, y, z, this);
					}
					if (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) >= ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
						if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
					}
                } else {
					if (((Entity) this instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) >= ((Entity) this instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
						if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
							datEntSetI.getEntityData().set(DATA_REVIVE_TICK, 0);
					}
				}
			} else {
				setShiftKeyDown(false);
			}
			target = (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null;
			if (dura > 0) {
				if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_DURATION, (int) (dura - 1));
			}
			if (sklp1 > 0) {
				if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
					datEntSetI.getEntityData().set(DATA_SKILL_P, (int) (sklp1 - 1));
			} else if (dura <= 0) {
				if (!(target == null) && target.isAlive()) {
					if (this instanceof OceanizedEnderDragonEntity) {
						this.setAnimation("chant");
					}
					if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_SKILL_P, 370);
					if ((Entity) this instanceof OceanizedEnderDragonEntity datEntSetI)
						datEntSetI.getEntityData().set(DATA_DURATION, 70);
					if (!this.level().isClientSide())
						this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 0, false, false));
					world.playSound(null, BlockPos.containing(x, y, z), CASounds.CASTER_SKILL.get(), SoundSource.HOSTILE, (float) 2.5, 1);
					for (int index0 = 0; index0 < 8; index0++) {
						CaerulaArborMod.queueServerWork(12 + index0 * 5, () -> {
							if (this.isAlive()) {
								MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
							}
						});
						if (P > 0.5) {
							CaerulaArborMod.queueServerWork(14 + index0 * 5, () -> {
								if (this.isAlive()) {
									MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
								}
							});
							CaerulaArborMod.queueServerWork(15 + index0 * 5, () -> {
								if (this.isAlive()) {
									MoistDragonBreathEntity.spawn(world, getX(), getY() + 3, getZ(), this, (Entity) this instanceof Mob mobEnt ? mobEnt.getTarget() : null, Mth.nextInt(RandomSource.create(), 0, 1));
								}
							});
						}
					}
				}
			}
			if (tickCount % 20 == 10) {
				if (!(target == null) && target.isAlive()) {
					this.destroyBlocks();
				}
			}
			if (tickCount % 400 == 100) {
				for (int index1 = 0; index1 < 2; index1++) {
					distributeCrystal(world, x, y, z);
				}
			}
			if (tickCount % 20 == 0) {
				Set<String> crystalUUIDs = this.crystals;
					Set<String> toDelete = new HashSet<>();
					for (String uuid1 : crystalUUIDs) {
						MoistEnderCrystalEntity crystal = this.getCrystal(uuid1);
						if (crystal != null && crystal.isAlive()) {
							if (this.getEntityData().get(DATA_REVIVE_TICK) <= 0) {
								this.crytsalToEnderina(crystal, this);
								EntityUtils.heal(this, this.getMaxHealth() * 0.01);
							} else {
								crystal.getNavigation().moveTo(getX(), getY(), getZ(), 0.5);
							}
						} else {
							toDelete.add(uuid1);
						}
					}
					crystalUUIDs.removeAll(toDelete);
			}
			if (rev > 0 && tickCount % 70 == 50) {
				distributeCrystal(world, x, y, z);
			}
			if (EntityUtils.getSpeed(this) > 2.0) {
				setDeltaMovement(new Vec3(0, 0, 0));
			}
		}
		this.refreshDimensions();
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.isAlive()) {
			if (!this.isReviving() && !this.isDeadOrDying()) {
				Vec3 movement = this.getDeltaMovement();
				if (movement.lengthSqr() > 1.0E-7D) {
					Vec3 movementDirection = movement.normalize();
					Vec3 facingDirection = new Vec3(
							Mth.sin(this.getYRot() * Mth.DEG_TO_RAD),
							movement.y,
							-Mth.cos(this.getYRot() * Mth.DEG_TO_RAD)
					).normalize();
					double damping = 0.8D + 0.15D * (movementDirection.dot(facingDirection) + 1.0D) / 2.0D;
					this.setDeltaMovement(movement.multiply(damping, 0.91D, damping));
				}
			}

			this.yBodyRot = this.getYRot();
			Vec3[] oldPositions = new Vec3[this.subEntities.length];
			for (int index = 0; index < this.subEntities.length; index++) {
				oldPositions[index] = this.subEntities[index].position();
			}

			if (this.posPointer < 0) {
				for (int index = 0; index < this.positions.length; index++) {
					this.positions[index][0] = this.getYRot();
					this.positions[index][1] = this.getY();
				}
			}
			if (++this.posPointer == this.positions.length) {
				this.posPointer = 0;
			}
			this.positions[this.posPointer][0] = this.getYRot();
			this.positions[this.posPointer][1] = this.getY();

			// applyWorldRotation=true：Helper 内部自动按 GeckoLib 官方 applyRotations 公式 (180°-yaw) 把所有骨骼位置绕 Y+ 做世界旋转，
			// 返回的 x/y/z 直接就是可丢给 tickPart 的世界偏移，不再需要在 Entity 端手搓 worldRot + .yRot()。
			Map<String, Vec3> allBonePos = this.serverGeoAnimator.tickAndGetCurrentPose(this.tickCount, this.getYRot(), true);

			// 从 Helper 返回的全量骨骼里挑出 subEntities 需要的部分
			Map<String, Vec3> currentPose = new HashMap<>();
			for (OceanizedEnderDragonPart part : this.subEntities) {
				Vec3 v = allBonePos.get(part.name);
				if (v != null) currentPose.put(part.name, v);
			}

			for (OceanizedEnderDragonPart part : this.subEntities) {
				Vec3 entityOffset = currentPose.get(part.name);
				if (entityOffset == null) continue;
				this.tickPart(part, entityOffset.x, entityOffset.y, entityOffset.z);
			}

			for (int index = 0; index < this.subEntities.length; index++) {
				OceanizedEnderDragonPart part = this.subEntities[index];
				Vec3 oldPosition = oldPositions[index];
				part.xo = oldPosition.x;
				part.yo = oldPosition.y;
				part.zo = oldPosition.z;
				part.xOld = oldPosition.x;
				part.yOld = oldPosition.y;
				part.zOld = oldPosition.z;
			}
		}
	}

	private void destroyBlocks() {
		Level world = this.level();
		if (!WorldUtils.canGrief(world)) {
			return;
		}
		boolean once = false;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		BlockPos originPos = this.blockPosition();
		double dx = -1;
		for (int index0 = 0; index0 < 3; index0++) {
			double dz = -1;
			for (int index1 = 0; index1 < 3; index1++) {
				double dy = 0;
				for (int index2 = 0; index2 < 2; index2++) {
					BlockPos blockPos = BlockPos.containing(x + dx, y + dy, z + dz);
					BlockState block = world.getBlockState(blockPos);
					if (!block.is(BlockTags.create(ResourceLocation.parse("minecraft:wither_immnue")))) {
						double hardness = block.getDestroySpeed(world, BlockPos.containing(0, 0, 0));
						if (hardness <= 7.5 && hardness >= 0 && world.getBlockFloorHeight(blockPos) > 0) {
							Block.dropResources(world.getBlockState(blockPos), world, originPos, null);
							world.destroyBlock(blockPos, false);
							world.updateNeighborsAt(blockPos, world.getBlockState(blockPos).getBlock());
							once = true;
						}
					}
					dy = dy + 1;
				}
				dz = dz + 1;
			}
			dx = dx + 1;
		}
		if (once) {
			if (!world.isClientSide()) {
				world.playSound(null, originPos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1);
			} else {
				world.playLocalSound(x, y, z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.NEUTRAL, 1, 1, false);
			}
		}
	}

	@Override
	public void performRangedAttack(LivingEntity target, float flval) {
		Vec3 p = this.getLookAngle().scale(0.45).reverse();
		this.push(p.x, 0, p.z);
		Level level = this.level();
		if (!level.isClientSide()) {
			level.playSound(this, this.blockPosition(), PRE, SoundSource.HOSTILE, 2, 1);
		} else {
			level.playLocalSound(this.getX(), this.getY(), this.getZ(), PRE, SoundSource.HOSTILE, 2, 1, false);
		}
		if (getPhase() == 0) {
			normalAttack(target);
		} else {
			superAttack(target);
		}
	}

	@Override
	public boolean canUsePortal(boolean allowVehicles) {
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
	public void customServerAiStep() {
		super.customServerAiStep();
		this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	private void tickPart(OceanizedEnderDragonPart part, double x, double y, double z) {
		part.setPos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 400);
		builder = builder.add(Attributes.ARMOR, 15);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.55);
		builder = builder.add(CAAttributes.MAGIC_RESISTANCE, 85);
		builder = builder.add(CAAttributes.GENERAL_DEFENSE, 4);
		builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.0125);
		builder = builder.add(CAAttributes.SANITY_RESISTANCE, 75);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ender_dragon.death"));
			}
			if (this.isReviving()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ender_dragon.revive"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_ender_dragon.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 25L <= level().getGameTime()) {
			this.swinging = false;
		}
		if ((this.swinging || this.entityData.get(DATA_SHOOT)) && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_ender_dragon.chant"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 40) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience(this.getKillCredit());
			Level world = this.level();
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
				if (world instanceof ServerLevel level) {
					ItemEntity entityToSpawn = new ItemEntity(level, x, (y + 1), z, new ItemStack(CAItems.MOIST_DRAGON_HEART.get()));
					entityToSpawn.setPickUpDelay(5);
					level.addFreshEntity(entityToSpawn);
				}
				for (int index0 = 0; index0 < 64; index0++) {
					if (world instanceof ServerLevel level)
						level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 48, 96)));
				}
			}
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(DATA_ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(DATA_ANIMATION, animation);
	}

	public int getDeathTextureTick() {
		return this.deathTime;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	private int getPhase() {
		return this.entityData.get(DATA_PHASE);
	}

	public boolean isReviving() {
		return this.entityData.get(DATA_REVIVE_TICK) > 0;
	}

	public boolean isEnderinaDurative() {
		return this.isAlive() && this.getEntityData().get(DATA_DURATION) <= 0 && this.getEntityData().get(DATA_REVIVE_TICK) <= 0;
	}

	private void normalAttack(LivingEntity target) {
		CaerulaArborMod.queueServerWork(11, () -> {
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
				}
		);
	}

	private void superAttack(LivingEntity target) {
		CaerulaArborMod.queueServerWork(11, () -> {
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 0);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
					MoistDragonBreathEntity.spawn(this.level(), this.getX(), this.getY() + 2.5, this.getZ(), this, target, 1);
				}
		);
	}

	private void distributeCrystal(LevelAccessor world, double x, double y, double z) {
		double r;
		double d;
		double tx;
		double tz;
		if (this.crystals.size() < 8) {
			r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
			d = Mth.nextDouble(RandomSource.create(), 7, 12);
			tx = x + d * Math.cos(r);
			tz = z + d * Math.sin(r);
			if (world instanceof ServerLevel level) {
				Entity entityToSpawn = CAEntities.MOIST_ENDER_CRYSTAL.get().spawn(level, BlockPos.containing(tx, y, tz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					this.putCrystal(entityToSpawn);
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (world instanceof ServerLevel level)
				level.sendParticles(ParticleTypes.EXPLOSION, tx, (y + 1), tz, 1, 0, 0, 0, 0.1);
		}
	}

	private void swallowNearbyCrystals() {
		Vec3 center = this.position();
		List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5 / 2d), entity -> true).stream()
				.sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
				.toList();
		for (Entity nearbyEntity : nearbyEntities) {
			if (nearbyEntity instanceof MoistEnderCrystalEntity && this.distanceTo(nearbyEntity) < 2.5) {
				if (!nearbyEntity.level().isClientSide()) {
					nearbyEntity.discard();
				}
				EntityUtils.heal(this, this.getMaxHealth() * 0.05F);
				if (this.level() instanceof ServerLevel level) {
					level.sendParticles(ParticleTypes.DRAGON_BREATH, nearbyEntity.getX(), nearbyEntity.getY() + 0.5, nearbyEntity.getZ(), 16, 0.5, 0.5, 0.5, 0.1);
				}
			}
		}
	}

	public void crytsalToEnderina(Entity me, Entity owner) {
		Vec3 ownerPos = owner.position();
		Vec3 goal;
		Vec3 v1 = ownerPos.vectorTo(me.position());
		Vec3 v = new Vec3(v1.x, 0, v1.z);
		if (v.lengthSqr() > 144)
			goal = ownerPos.add(v.normalize().scale(10));
		else {
			RandomSource random = me.level().random;
			int yaw = Mth.nextInt(random, 30, 90);
			double r = Mth.nextDouble(random, 7, 12);
			goal = ownerPos.add(v.normalize().scale(r).yRot((float) Math.toRadians(yaw)));
		}
		if (goal.distanceToSqr(me.position()) > 0.25) {
			Vec3 dir = goal.subtract(me.position()).normalize();
			double speed = me instanceof Mob mob ? mob.getAttributeValue(Attributes.FLYING_SPEED) : 0.45;
			me.setDeltaMovement(dir.scale(speed));
		}
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}

	class DragonWanderGoal extends Goal {
		private final OceanizedEnderDragonEntity dragon;
		private final double speedModifier;
		private double targetX;
		private double targetY;
		private double targetZ;
		private int nextRecalcTick;

		public DragonWanderGoal(OceanizedEnderDragonEntity dragon, double speedModifier) {
			this.dragon = dragon;
			this.speedModifier = speedModifier;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			if (dragon.getTarget() != null) return false;
			if (!dragon.isEnderinaDurative()) return false;
			if (dragon.tickCount < this.nextRecalcTick) return false;
			Vec3 target = this.pickRandomTarget();
			if (target == null) return false;
			this.targetX = target.x;
			this.targetY = target.y;
			this.targetZ = target.z;
			return true;
		}

		@Override
		public boolean canContinueToUse() {
			double dx = dragon.getX() - this.targetX;
			double dy = dragon.getY() - this.targetY;
			double dz = dragon.getZ() - this.targetZ;
			return dragon.getTarget() == null
				&& dragon.isEnderinaDurative()
				&& Mth.square(dx) + Mth.square(dy) + Mth.square(dz) > 4.0D;
		}

		@Override
		public void start() {
			this.nextRecalcTick = dragon.tickCount + 1;
		}

		@Override
		public void tick() {
			dragon.getMoveControl().setWantedPosition(this.targetX, this.targetY, this.targetZ, this.speedModifier);
		}

		@Override
		public void stop() {
			dragon.getMoveControl().setWantedPosition(dragon.getX(), dragon.getY(), dragon.getZ(), 0.0D);
		}

		@Nullable
		private Vec3 pickRandomTarget() {
			RandomSource r = dragon.getRandom();
			float angle = r.nextFloat() * Mth.TWO_PI;
			float radius = 25 + r.nextFloat() * 15;
			double dx = Mth.cos(angle) * radius;
			double dz = Mth.sin(angle) * radius;
			double dy = (r.nextFloat() - 0.5F) * 30;
			Level level = dragon.level();
			double y = Mth.clamp(dragon.getY() + dy, level.getMinBuildHeight() + 10, level.getMaxBuildHeight() - 20);
			return new Vec3(dragon.getX() + dx, y, dragon.getZ() + dz);
		}
	}

	class DoNothingGoal extends Goal {
		public DoNothingGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
		}

		public boolean canUse() {
			return OceanizedEnderDragonEntity.this.getEntityData().get(DATA_REVIVE_TICK) > 0;
		}
	}

}