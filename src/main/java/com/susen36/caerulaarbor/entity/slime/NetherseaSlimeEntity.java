package com.susen36.caerulaarbor.entity.slime;

import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetherseaSlimeEntity extends AbstractSeaSlimeEntity {
	public static final EntityDataAccessor<Boolean> DATA_SHOOT = AbstractSeaSlimeEntity.DATA_SHOOT;
	public static final EntityDataAccessor<String> DATA_ANIMATION = AbstractSeaSlimeEntity.DATA_ANIMATION;
	public static final EntityDataAccessor<Integer> DATA_SIZE = AbstractSeaSlimeEntity.DATA_SIZE;

	public NetherseaSlimeEntity(Level world) {
		this(CAEntities.NETHERSEA_SLIME.get(), world);
	}

	public NetherseaSlimeEntity(EntityType<NetherseaSlimeEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(3, new FloatGoal(this));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1));
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.nethersea_slime";
	}

	@Override
	protected int getSplitSizeThreshold() {
		return 1;
	}

	@Override
	protected int getSplitMinCount() {
		return 2;
	}

	@Override
	protected int getSplitMaxCount() {
		return 4;
	}

	@Override
	protected int computeSplitChildSize(int parentSize, int spawnIndex) {
		return (int) (parentSize * 0.5);
	}

	@Override
	protected EntityType<? extends AbstractSeaSlimeEntity> getSplitEntityType() {
		return CAEntities.NETHERSEA_SLIME.get();
	}

	public boolean isTiny() {
		return this.entityData.get(DATA_SIZE) <= 1;
	}

	@Override
	protected void dropLoot(Level world, Vec3 pos) {
		if (world instanceof ServerLevel level) {
			ItemEntity entityToSpawn = new ItemEntity(level, pos.x, pos.y, pos.z, new ItemStack(CAItems.TRAIL_CREAM.get()));
			entityToSpawn.setPickUpDelay(10);
			level.addFreshEntity(entityToSpawn);
		}
	}

	@Override
	protected void onPushEntity(Entity pEntity) {
		if (pEntity instanceof LivingEntity entity && !entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(CAMobEffects.DEDUCT_ONE_SANITY, 70, 0));
			if (this.isEffectiveAi()) {
				this.dealDamage(entity);
			}
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.075);
		builder = builder.add(Attributes.MAX_HEALTH, 2);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 24);
		builder = builder.add(Attributes.STEP_HEIGHT, 1f);
		return builder;
	}
}
