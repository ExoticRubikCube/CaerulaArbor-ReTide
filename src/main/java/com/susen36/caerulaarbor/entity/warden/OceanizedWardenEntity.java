package com.susen36.caerulaarbor.entity.warden;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public class OceanizedWardenEntity extends AbstractOceanizedWardenEntity {
	public OceanizedWardenEntity(Level world) {
		this(CAEntities.OCEANIZED_WARDEN.get(), world);
	}

	public OceanizedWardenEntity(EntityType<OceanizedWardenEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
	}

	@Override
        protected SoundEvent getAmbientSound() {
                return SoundEvents.WARDEN_AMBIENT;
	}

	@Override
        protected SoundEvent getHurtSound(DamageSource source) {
                return SoundEvents.WARDEN_HURT;
	}

	@Override
        protected SoundEvent getDeathSound() {
                return SoundEvents.WARDEN_DEATH;
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.oceanized_warden";
	}

	@Override
	protected int getAttackAnimationLength() {
		return 18;
	}

	@Override
	protected int getInitialHeartbeatGap() {
		return 40;
	}
}