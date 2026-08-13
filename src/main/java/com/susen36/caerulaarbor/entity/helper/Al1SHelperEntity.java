package com.susen36.caerulaarbor.entity.helper;

import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class Al1SHelperEntity extends LittleHelperEntity {
	public Al1SHelperEntity(Level world) {
		this(CAEntities.AL_1_S_HELPER.get(), world);
	}

	public Al1SHelperEntity(EntityType<? extends LittleHelperEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected InteractionResult handleApocalypseInteract(Player sourceentity) {
		return InteractionResult.PASS;
	}

	@Override
    public void playPassengerLeftClickSound(Player passenger) {
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(passenger.getX(), passenger.getY(), passenger.getZ()),
					CASounds.AL1S_WORK.get(), SoundSource.BLOCKS, 3, 1);
		}
	}

	@Override
	protected ItemStack getRecycleItemStack() {
		return new ItemStack(CAItems.ITEM_HELPER_AL_1S.get());
	}

	@Override 
	protected void playBreakSound(ServerLevel serverLevel) {
		serverLevel.playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
				CASounds.AL1S_BREAK.get(), SoundSource.BLOCKS, 3, 1);
	}

	@Override
	@Nullable
	protected SoundEvent getCustomDeathSound() {
		return null;
	}
}