package com.susen36.caerulaarbor.entity.enderdragon;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.api.anim.ServerGeoAnimator;
import com.susen36.caerulaarbor.client.model.entity.OceanizedEnderDragonModel;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class OceanizedEnderDragonEntity extends AbstractOceanizedEnderDragonEntity {

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

	private final ServerGeoAnimator<OceanizedEnderDragonEntity> serverGeoAnimator;

	public OceanizedEnderDragonEntity(Level world) {
		this(CAEntities.OCEANIZED_ENDER_DRAGON.get(), world);
	}

	public OceanizedEnderDragonEntity(EntityType<OceanizedEnderDragonEntity> type, Level world) {
		super(type, world);
		this.serverGeoAnimator = new ServerGeoAnimator<>(this, new OceanizedEnderDragonModel());
		this.head = new OceanizedEnderDragonPart(this, "head", 1.25F, 1.25F);
		this.neck1 = new OceanizedEnderDragonPart(this, "neck2", 1.5F, 1.75F);
		this.neck2 = new OceanizedEnderDragonPart(this, "neck4", 1.5F, 1.75F);
		this.body = new OceanizedEnderDragonPart(this, "body",false, 5.0F, 3.0F);
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
		this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);
	}



	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(5, new DragonWanderGoal(this, 1.25D));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
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

	public boolean hurt(OceanizedEnderDragonPart part, DamageSource source, float amount) {
		return this.hurt(source, amount);
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

			Map<String, Vec3> allBonePos = this.serverGeoAnimator.tickAndGetCurrentPose(this.tickCount, this.getYRot(), true);

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

	@Override
	public void baseTick() {
		super.baseTick();
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		if (this.isAlive()) {
			if (tickCount % 60 == 0) {
				if (WorldUtils.hasNoSolidGroundBelow(world, x, y, z, 5)) {
					push(0, (0.35), 0);
				}
				if (WorldUtils.hasNoSolidGroundBelow(world, x, y, z, 18)) {
					push(0, (-0.35), 0);
				}
			}
		}
	}

	@Override
	protected PathNavigation createNavigation(Level pLevel) {
		HighAltitudeFlyingPathNavigation flyingpathnavigation = new HighAltitudeFlyingPathNavigation(this, pLevel);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(true);
		return flyingpathnavigation;
	}

	@Override
	protected Vec3 getBreathSpawnPos() {
		return new Vec3(this.head.getX(), this.head.getY(0.5), this.head.getZ());
	}


	private void tickPart(OceanizedEnderDragonPart part, double x, double y, double z) {
		part.setPos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	@Override
	protected void destroyBlocks() {
		Level world = this.level();
		if (WorldUtils.canGrief(world)) {
			boolean once = false;
			double x = this.getX();
			double y = this.getY();
			double z = this.getZ();
			BlockPos originPos = this.blockPosition();
			double dx = -1;
			for (int index0 = 0; index0 < 4; index0++) {
				double dz = -1;
				for (int index1 = 0; index1 < 4; index1++) {
					double dy = 0;
					for (int index2 = 0; index2 < 3; index2++) {
						BlockPos blockPos = BlockPos.containing(x + dx, y + dy, z + dz);
						BlockState block = world.getBlockState(blockPos);
						if (!block.is(BlockTags.DRAGON_TRANSPARENT)) {
							double hardness = block.getDestroySpeed(world, blockPos);
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
	}


	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData livingdata) {
		super.finalizeSpawn(world, difficulty, reason, livingdata);
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 100, 9, false, false));
		//this.setAnimation("animation.oceanized_ender_dragon.reviveToFly");
		return livingdata;
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.oceanized_ender_dragon";
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.45);
		builder = builder.add(Attributes.MAX_HEALTH, 600);
		builder = builder.add(Attributes.ARMOR, 15);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 14);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.55);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 85);
		builder = builder.add(CAAttributes.GENERAL_DEFENSE, 4);
		builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0);
		builder = builder.add(CAAttributes.SANITY_RESISTANCE, 75);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}

	static class DragonWanderGoal extends WaterAvoidingRandomStrollGoal {
		private final OceanizedEnderDragonEntity dragon;
		private final double speedModifier;
		private double targetX;
		private double targetY;
		private double targetZ;
		private int nextRecalcTick;

		public DragonWanderGoal(OceanizedEnderDragonEntity dragon, double speedModifier) {
			super(dragon, speedModifier);
			this.dragon = dragon;
			this.speedModifier = speedModifier;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Nullable
		protected Vec3 getPosition() {
			Vec3 vec3 = this.mob.getViewVector(0.0F);
			Vec3 vec31 = HoverRandomPos.getPos(this.mob, 8, 7, vec3.x, vec3.z, Mth.PI / 2.0F, 8, 4);
			return vec31 != null ? vec31 : AirAndWaterRandomPos.getPos(this.mob, 8, 4, 2, vec3.x, vec3.z, Mth.PI / 2.0F);
		}

		@Override
		public boolean canUse() {
			if (dragon.getTarget() != null) return false;
			if (!dragon.isDurative()) return false;
			if (dragon.tickCount < this.nextRecalcTick) return false;
			Vec3 target = this.getPosition();
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
				&& dragon.isDurative()
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

	}

	public static class HighAltitudeFlyNodeEvaluator extends FlyNodeEvaluator {
		@Override
		public PathType getPathType(PathfindingContext context, int x, int y, int z) {
			PathType pathtype = context.getPathTypeFromState(x, y, z);
			if (pathtype == PathType.OPEN && y >= context.level().getMinBuildHeight() + 1) {
				BlockPos blockpos = new BlockPos(x, y - 1, z);
				PathType pathtype1 = context.getPathTypeFromState(blockpos.getX(), blockpos.getY(), blockpos.getZ());
				if (pathtype1 != PathType.DAMAGE_FIRE && pathtype1 != PathType.LAVA) {
					if (pathtype1 == PathType.DAMAGE_OTHER) {
						pathtype = PathType.DAMAGE_OTHER;
					} else if (pathtype1 == PathType.COCOA) {
						pathtype = PathType.COCOA;
					} else if (pathtype1 == PathType.FENCE) {
						if (!blockpos.equals(context.mobPosition())) {
							pathtype = PathType.FENCE;
						}
					} else {
						pathtype = pathtype1 != PathType.WALKABLE && pathtype1 != PathType.OPEN && pathtype1 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
					}
				} else {
					pathtype = PathType.DAMAGE_FIRE;
				}
			}

			if (pathtype == PathType.WALKABLE || pathtype == PathType.OPEN) {
				pathtype = checkNeighbourBlocks(context, x, y, z, pathtype);
			}

			int distToGround;
			if (context.level() instanceof Level level) {
				int groundY = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY();
				distToGround = y - groundY;
			} else {
				distToGround = 14;
			}

			if (distToGround < 7 || distToGround > 14) {
				return PathType.DANGER_OTHER;
			}

			return pathtype;
		}
	}

	public static class HighAltitudeFlyingPathNavigation extends FlyingPathNavigation {
		public HighAltitudeFlyingPathNavigation(Mob mob, Level level) {
			super(mob, level);
		}

		@Override
		protected PathFinder createPathFinder(int maxVisitedNodes) {
			this.nodeEvaluator = new HighAltitudeFlyNodeEvaluator();
			this.nodeEvaluator.setCanPassDoors(true);
			return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
		}
	}
}
