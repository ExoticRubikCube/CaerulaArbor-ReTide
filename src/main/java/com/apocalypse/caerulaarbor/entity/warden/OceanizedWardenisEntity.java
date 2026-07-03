package com.apocalypse.caerulaarbor.entity.warden;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.nbt.CompoundTag;
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
	protected String getAmbientSoundId() {
		return CaerulaArborMod.MODID + ":wardenis_idle";
	}

	@Override
	protected String getHurtSoundId() {
		return CaerulaArborMod.MODID + ":wardenis_hurt";
	}

	@Override
	protected String getDeathSoundId() {
		return CaerulaArborMod.MODID + ":wardenis_die";
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
