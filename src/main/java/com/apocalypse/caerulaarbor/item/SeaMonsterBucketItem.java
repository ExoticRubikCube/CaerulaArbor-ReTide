package com.apocalypse.caerulaarbor.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class SeaMonsterBucketItem extends MobBucketItem {
	public SeaMonsterBucketItem(EntityType<?> entityType, SoundEvent emptySound, Item.Properties properties) {
		super(entityType, Fluids.WATER, emptySound, properties);
	}

	@Override
	public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult hitResult) {
		BlockState blockState = level.getBlockState(pos);
		if (!blockState.canBeReplaced()) {
			return hitResult != null && this.emptyContents(player, level, hitResult.getBlockPos().relative(hitResult.getDirection()), null);
		}

		this.playEmptySound(player, level, pos);
		return true;
	}
}
