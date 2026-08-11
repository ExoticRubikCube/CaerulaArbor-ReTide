package com.susen36.caerulaarbor.entity.slime;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.ApostleProkaryoteEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.EnumSet;

public class FissionProkaryoteSlimeEntity extends AbstractSeaSlimeEntity {
    public static final EntityDataAccessor<Boolean> DATA_SHOOT = AbstractSeaSlimeEntity.DATA_SHOOT;
    public static final EntityDataAccessor<String> DATA_ANIMATION = AbstractSeaSlimeEntity.DATA_ANIMATION;
    public static final EntityDataAccessor<Integer> DATA_SIZE = AbstractSeaSlimeEntity.DATA_SIZE;
    private boolean wasOnGround = false;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    private final MoveControl landControl;
    private final ApostleProkaryoteEntity.SeabornSwimControl swimControl;

    public FissionProkaryoteSlimeEntity(Level world) {
        this(CAEntities.FISSION_PROKARYOTE_SLIME.get(), world);
    }

    public FissionProkaryoteSlimeEntity(EntityType<FissionProkaryoteSlimeEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.WATER, 0);
        this.landControl = new SlimeMoveControl(this);
        this.swimControl = new ApostleProkaryoteEntity.SeabornSwimControl(this);
        this.moveControl = this.landControl;
        this.waterNavigation = new WaterBoundPathNavigation(this, world);
        this.groundNavigation = new GroundPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FissionProkaryoteSlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new FissionProkaryoteSlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new FissionProkaryoteSlimeKeepOnJumpingGoal(this));
        this.goalSelector.addGoal(9, new RandomSwimmingGoal(this, 1, 40));
        this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
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

    public void updateSwimming() {
        if (!this.level().isClientSide()) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.moveControl = this.swimControl;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.moveControl = this.landControl;
                this.setSwimming(false);
            }
        }
    }

    private boolean hasSilenceBoost() {
        return MapVariables.get(this.level()).strategy_silence > 0;
    }

    @Override
	protected void refreshAttributesBySize(int size) {
		super.refreshAttributesBySize(size);
		if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D + 0.1D * (double) size);
		}
	}

	public void jumpFromGround() {
		Vec3 vec3 = this.getDeltaMovement();
		double jumpPower = this.getJumpPower();
		if (this.hasSilenceBoost()) {
			jumpPower *= 1.25D;
		}
		this.setDeltaMovement(vec3.x, jumpPower, vec3.z);
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
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected String getAnimationPrefix() {
        return "animation.fission_prokaryote_slime";
    }

    @Override
    protected int getSplitSizeThreshold() {
        return 3;
    }

    @Override
    protected int getSplitMinCount() {
        return 3;
    }

    @Override
    protected int getSplitMaxCount() {
        return 4;
    }

    @Override
    protected int computeSplitChildSize(int parentSize, int spawnIndex) {
        int halfSize = (int) (parentSize * 0.5);
        return Mth.clamp(halfSize + spawnIndex, 1, parentSize - 1);
    }

    @Override
    protected EntityType<? extends AbstractSeaSlimeEntity> getSplitEntityType() {
        return CAEntities.FISSION_PROKARYOTE_SLIME.get();
    }

    public boolean isTiny() {
        return this.entityData.get(DATA_SIZE) <= 2;
    }

    @Override
    protected void onPushEntity(Entity pEntity) {
        if (pEntity.is(this.getTarget()) && this.isEffectiveAi()) {
            this.dealDamage((LivingEntity) pEntity);
        }
    }

    @Override
    protected void dropLoot(Level world, Vec3 pos) {
        if (world instanceof ServerLevel level) {
            int r = this.random.nextInt(10);
            if (r < 2) {
                ItemEntity entityToSpawn = new ItemEntity(level, pos.x, pos.y, pos.z, new ItemStack(CAItems.BROKEN_OCEAN_CELL.get()));
                entityToSpawn.setPickUpDelay(10);
                level.addFreshEntity(entityToSpawn);
            } else if (r < 5) {
                ItemEntity entityToSpawn = new ItemEntity(level, pos.x, pos.y, pos.z, new ItemStack(CAItems.OCEAN_CELL.get()));
                entityToSpawn.setPickUpDelay(10);
                level.addFreshEntity(entityToSpawn);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder = builder.add(Attributes.MAX_HEALTH, 3);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2.5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 36);
        builder = builder.add(Attributes.STEP_HEIGHT, 1.25f);
        builder = builder.add(Attributes.JUMP_STRENGTH, 0.49F);
        builder = builder.add(NeoForgeMod.SWIM_SPEED, 0.8D);
        return builder;
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
                    float base = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    float distMul = this.slime.hasSilenceBoost() ? 1.25F : 1.0F;
                    this.mob.setSpeed(base * distMul);
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }
                        this.slime.getJumpControl().jump();
                        if (this.slime.doPlayJumpSound()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    float base = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    float distMul = this.slime.hasSilenceBoost() ? 1.25F : 1.0F;
                    this.mob.setSpeed(base * distMul);
                }
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
            return !this.slime.isPassenger() && !this.slime.isInWater() && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setWantedMovement(1.0);
            }
        }
    }

    static class FissionProkaryoteSlimeAttackGoal extends Goal {
        private final FissionProkaryoteSlimeEntity slime;
        private int growTiredTimer;

        public FissionProkaryoteSlimeAttackGoal(FissionProkaryoteSlimeEntity pEntity) {
            this.slime = pEntity;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.slime.getTarget();
            return livingentity != null && this.slime.canAttack(livingentity);
        }

        @Override
        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.slime.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.slime.getTarget();
            if (livingentity != null) {
                this.slime.lookAt(livingentity, 10.0F, 10.0F);
                if (this.slime.isInWater()) {
                    this.slime.getNavigation().moveTo(livingentity, 1.2);
                } else if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                    control.setDirection(this.slime.getYRot(), this.slime.isEffectiveAi());
                }
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
            if (this.slime.getTarget() != null) {
                return false;
            } else if (!this.slime.onGround() && !this.slime.isInWater() && !this.slime.isInLava()) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                this.chosenDegrees = this.slime.getRandom().nextInt(360);
            }
            if (this.slime.isInWater()) {
                double rad = Math.toRadians(this.chosenDegrees);
                double tx = this.slime.getX() + Math.cos(rad) * 8;
                double tz = this.slime.getZ() + Math.sin(rad) * 8;
                double ty = this.slime.getY() + (this.slime.getRandom().nextDouble() - 0.5) * 4;
                this.slime.getNavigation().moveTo(tx, ty, tz, 0.8);
            } else if (this.slime.getMoveControl() instanceof SlimeMoveControl control) {
                control.setDirection(this.chosenDegrees, false);
            }
        }
    }
}
