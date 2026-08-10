package com.susen36.caerulaarbor.entity.warden;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class OceanizedWardenEntity extends AbstractOceanizedWardenEntity {
	public OceanizedWardenEntity(Level world) {
		this(CAEntities.OCEANIZED_WARDEN.get(), world);
	}

	public OceanizedWardenEntity(EntityType<OceanizedWardenEntity> type, Level world) {
		super(type, world);
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