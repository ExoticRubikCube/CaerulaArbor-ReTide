package com.susen36.caerulaarbor.entity.enderdragon;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class OceanizedEnderinaEntity extends AbstractOceanizedEnderDragonEntity {

	public OceanizedEnderinaEntity(Level world) {
		this(CAEntities.OCEANIZED_ENDERINA.get(), world);
	}

	public OceanizedEnderinaEntity(EntityType<OceanizedEnderinaEntity> type, Level world) {
		super(type, world);
		xpReward = 128;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 32.0F));
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
				if (WorldUtils.hasNoSolidGroundBelow(world, x, y, z, 14)) {
					push(0, (-0.35), 0);
				}
			}
		}
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData livingdata) {
		super.finalizeSpawn(world, difficulty, reason, livingdata);
		if (!this.level().isClientSide())
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 50, 9, false, false));
		this.setAnimation("animation.oceanized_enderina.start");
		return livingdata;
	}

	@Override
	protected Vec3 getBreathSpawnPos() {
		return new Vec3(this.getX(), this.getY() + 2.5, this.getZ());
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.oceanized_enderina";
	}

	@Override
	public void die(DamageSource source) {
		// 复活期被非海嗣、非玩家的生物击杀时淬变为末影龙，不触发战利品与经验掉落
		Entity attacker = source.getEntity();
		if (this.isReviving() && attacker instanceof LivingEntity living
				&& !(living instanceof Player)
				&& !attacker.getType().is(CAEntityTypeTags.SEABORN)) {
			if (this.level() instanceof ServerLevel) {
				Mob converted = this.convertTo(CAEntities.OCEANIZED_ENDER_DRAGON.get(), false);
				if (converted != null) {
					converted.setYRot(this.getYRot());
					converted.setYBodyRot(this.getYRot());
					converted.setYHeadRot(this.getYRot());
					converted.setXRot(this.getXRot());
				}
				this.setRemoved(RemovalReason.DISCARDED);
			}
			return;
		}
		super.die(source);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.41);
		builder = builder.add(Attributes.MAX_HEALTH, 400);
		builder = builder.add(Attributes.ARMOR, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
		builder = builder.add(Attributes.FOLLOW_RANGE, 36);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
		builder = builder.add(Attributes.FLYING_SPEED, 0.55);
		builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 75);
		builder = builder.add(BabelAttributes.ELEMENTAL_MODIFIER, 0.125);
		builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
		return builder;
	}
}
