package com.susen36.caerulaarbor.entity.slime;

import com.susen36.caerulaarbor.entity.base.SeaMonster;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FissionProkaryoteSlimeEntity  extends SeaMonster {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = SynchedEntityData.defineId(FissionProkaryoteSlimeEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(FissionProkaryoteSlimeEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(FissionProkaryoteSlimeEntity.class, EntityDataSerializers.INT);
    public String animationprocedure = "empty";
    private boolean wasOnGround = false;

    public FissionProkaryoteSlimeEntity(Level world) {
        this(CAEntities.FISSION_PROKARYOTE_SLIME.get(), world);
    }

    public FissionProkaryoteSlimeEntity(EntityType<FissionProkaryoteSlimeEntity> type, Level world) {
        super(type, world);
        xpReward = 2;
        setNoAi(false);
        this.moveControl = new SlimeMoveControl(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOOT, false);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_SIZE, 5);
    }

    @Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(5, new FissionProkaryoteSlimeKeepOnJumpingGoal(this));
		this.goalSelector.addGoal(4, new FissionProkaryoteSlimeHopGoal(this));
		this.goalSelector.addGoal(3, new FissionProkaryoteSlimeFloatGoal(this));
		this.goalSelector.addGoal(2, new FissionProkaryoteSlimeRandomDirectionGoal(this));
		this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
	}

    @Override
    public void tick() {
        super.tick();
        if (this.onGround() && !this.wasOnGround) {
            float pitch = (((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F) * 0.5F;
            this.playSound(SoundEvents.SLIME_SQUISH, 0.2F, pitch);
        } else if (!this.onGround() && this.wasOnGround) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), this.getSoundPitch() * 0.75F);
        }
        this.wasOnGround = this.onGround();
    }

    public void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
        CommonHooks.onLivingJump(this);
    }

	protected int getJumpDelay() {
		return this.random.nextInt(30) + 20;
	}

    protected SoundEvent getJumpSound() {
        return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

	float getSoundPitch() {
		float f = this.isTiny() ? 1.2F : 0.6F;
		return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
	}

	protected boolean doPlayJumpSound() {
		return this.isEffectiveAi();
	}

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SLIME_JUMP_SMALL, 0.2f, 1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SLIME_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.SLIME_DEATH;
    }

    @Override
    public boolean causeFallDamage(float l, float damage, DamageSource source) {
        this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1, 1);
        return super.causeFallDamage(l, damage, source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Size", this.entityData.get(DATA_SIZE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Size")) {
            this.entityData.set(DATA_SIZE, compound.getInt("Size"));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);
        RandomSource random = world.getRandom();
        int i = random.nextInt(3);
        if (i < 2 && random.nextFloat() < 0.5F * difficulty.getSpecialMultiplier()) {
            i++;
        }
        int size = 1 << i;
        this.entityData.set(DATA_SIZE, size);
        return retval;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        CompoundTag tag = this.getPersistentData();
        if(!tag.getBoolean("Resized")){
            double size;
            size = (Entity) this instanceof FissionProkaryoteSlimeEntity datEntI ? datEntI.getEntityData().get(DATA_SIZE) : 0;
            if (size > 1) {
                if (this.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(
                            ((this.getAttributes().hasAttribute(Attributes.MAX_HEALTH) ? this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : 0) * Math.pow(size, 2)));
                if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
                    this.getAttribute(Attributes.ATTACK_DAMAGE)
                            .setBaseValue(((this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : 0) * size));
                if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED))
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((0.1 + 0.025 * Math.max(size, 4)));
                if (this.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE))
                    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue((size * 0.2));
                this.setHealth(this.getMaxHealth());
            }
            tag.putBoolean("Resized", true);
        }
        this.refreshDimensions();
    }

    public double getSlimeSize() {
        return this.getEntityData().get(DATA_SIZE) * 0.5;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_33597_) {
        return super.getDefaultDimensions(p_33597_).scale((float) this.getSlimeSize());
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.05F && event.getLimbSwingAmount() < 0.05F))) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.fission_prokaryote.move"));
            }
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.fission_prokaryote.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.fission_prokaryote.idle"));
        }
        return PlayState.STOP;
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
        if (this.deathTime >= 14) {
            this.remove(FissionProkaryoteSlimeEntity.RemovalReason.KILLED);
            this.dropExperience(this.getKillCredit());
            LevelAccessor world = this.level();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            int originalSize = this.getEntityData().get(DATA_SIZE);
            if (originalSize > 2) {
                int halfSize = (int) (originalSize * 0.5);
                Vec3 pos = new Vec3(x, y, z);
                if (world instanceof ServerLevel level) {
                    RandomSource levelRandom = level.getRandom();
                    int t = Mth.nextInt(levelRandom, 3, 4);
                    for (int index0 = 0; index0 < t; index0++) {
                        Vec3 offset = new Vec3(Mth.nextDouble(levelRandom, -1, 1), 0, Mth.nextDouble(levelRandom, -1, 1));
                        Entity entityToSpawn = CAEntities.FISSION_PROKARYOTE_SLIME.get().create(level);
                        if (entityToSpawn instanceof FissionProkaryoteSlimeEntity slime){
                            slime.setPos(pos.add(offset));
                            slime.getEntityData().set(DATA_SIZE, Math.min(halfSize + index0, originalSize));
                            slime.setYRot(world.getRandom().nextFloat() * 360F);
                            level.addFreshEntity(slime);
                        }
                    }
                }
            } else if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(CAItems.TRAIL_CREAM.get()));
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
        }
    }

    public boolean isTiny() {
        return this.entityData.get(DATA_SIZE) <= 2;
    }

    protected boolean isDealsDamage() {
        return !this.isTiny() && this.isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void playerTouch(Player player) {
        if (this.isDealsDamage()) {
            this.dealDamage(player);
        }
    }

    @Override
    public void push(Entity pEntity) {
        super.push(pEntity);
        if (pEntity.is(this.getTarget()) && this.isDealsDamage()) {
            this.dealDamage((LivingEntity) pEntity);
        }
    }

    protected void dealDamage(LivingEntity target) {
        if (this.isAlive() && this.isWithinMeleeAttackRange(target) && this.hasLineOfSight(target)) {
            DamageSource damagesource = this.damageSources().mobAttack(this);
            if (target.hurt(damagesource, this.getAttackDamage())) {
                target.invulnerableTime = 0;
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                Level var4 = this.level();
                if (var4 instanceof ServerLevel serverlevel) {
                    EnchantmentHelper.doPostAttackEffects(serverlevel, target, damagesource);
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.065);
        builder = builder.add(Attributes.MAX_HEALTH, 3);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2.5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.25f);
        builder = builder.add(Attributes.JUMP_STRENGTH, 0.49F);
        return builder;
    }

    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }

    static class SlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final FissionProkaryoteSlimeEntity slime;
        private boolean isAggressive;

        public SlimeMoveControl(FissionProkaryoteSlimeEntity pEntity) {
            super(pEntity);
            this.slime = pEntity;
            this.yRot = 180.0F * pEntity.getYRot() / (float) Math.PI;
        }

        public void setDirection(float pDir, boolean pAggressive) {
            this.yRot = pDir;
            this.isAggressive = pAggressive;
        }

        public void setWantedMovement(double pSpeed) {
            this.speedModifier = pSpeed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }
                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                        this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class FissionProkaryoteSlimeHopGoal extends Goal {
        private final FissionProkaryoteSlimeEntity slime;

        public FissionProkaryoteSlimeHopGoal(FissionProkaryoteSlimeEntity pEntity) {
            this.slime = pEntity;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.slime.onGround();
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().jump();
            }
            if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setWantedMovement(1.2);
            }
        }
    }

    static class FissionProkaryoteSlimeKeepOnJumpingGoal extends Goal {
        private final FissionProkaryoteSlimeEntity slime;

        public FissionProkaryoteSlimeKeepOnJumpingGoal(FissionProkaryoteSlimeEntity pEntity) {
            this.slime = pEntity;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        @Override
        public void tick() {
            if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setWantedMovement(1.0);
            }
        }
    }

    static class FissionProkaryoteSlimeFloatGoal extends Goal {
        private final FissionProkaryoteSlimeEntity slime;

        public FissionProkaryoteSlimeFloatGoal(FissionProkaryoteSlimeEntity pEntity) {
            this.slime = pEntity;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            pEntity.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setWantedMovement(1.2);
            }
        }
    }

    static class FissionProkaryoteSlimeRandomDirectionGoal extends Goal {
        private final FissionProkaryoteSlimeEntity slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public FissionProkaryoteSlimeRandomDirectionGoal(FissionProkaryoteSlimeEntity pEntity) {
            this.slime = pEntity;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.slime.getTarget() == null
                && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava())
                && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = this.slime.getRandom().nextInt(360);
            }
            if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setDirection(this.chosenDegrees, false);
            }
        }
    }
}