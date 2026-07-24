package com.susen36.caerulaarbor.entity.warden;

import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class OceanizedWardenisEntity extends AbstractOceanizedWardenEntity {
	public OceanizedWardenisEntity(Level world) {
		this(CAEntities.OCEANIZED_WARDENIS.get(), world);
	}

	public OceanizedWardenisEntity(EntityType<OceanizedWardenisEntity> type, Level world) {
		super(type, world);
	}

	@Override
        protected SoundEvent getAmbientSoundEvent() {
                return CASounds.WARDENIS_IDLE.get();
	}

	@Override
        protected SoundEvent getHurtSoundEvent() {
                return CASounds.WARDENIS_HURT.get();
	}

	@Override
        protected SoundEvent getDeathSoundEvent() {
                return CASounds.WARDENIS_DIE.get();
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.oceanized_wardenis";
	}

	@Override
	protected int getAttackAnimationLength() {
		return 19;
	}

	@Override
	protected int getInitialHeartbeatGap() {
		return 0;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata, @Nullable CompoundTag tag) {
		SpawnGroupData spawnGroupData = super.finalizeSpawn(world, difficulty, reason, livingdata, tag);
		this.setAnimation("animation.oceanized_wardenis.start");
		this.getEntityData().set(DATA_DURATION, 80);
		if (!this.level().isClientSide()) {
			this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE.get(), 80, 5, false, false));
		}
		return spawnGroupData;
	}
}
