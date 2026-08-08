package com.susen36.caerulaarbor.entity.base;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SeaMonster extends Monster implements GeoEntity, SyncedAnimationEntity {
	private static final TagKey<Block> NETHERSEA_WALKER = BlockTags.create(
			ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_functions"));
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected SeaMonster(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	protected float getBlockSpeedFactor() {
		float baseFactor = super.getBlockSpeedFactor();
		BlockPos belowPos = this.getBlockPosBelowThatAffectsMyMovement();
		BlockState belowState = this.level().getBlockState(belowPos);
		if (belowState.is(NETHERSEA_WALKER)) {
			double migrationLevel = MapVariables.get(this.level()).strategy_migration;
			if (migrationLevel > 0.0) {
				return baseFactor * (float) (1.0 + 0.05 * migrationLevel);
			}
		}
		return baseFactor;
	}
}