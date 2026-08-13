package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GoldenChaliseItem extends CollectibleItem.CustomCollectibleItem {
	public GoldenChaliseItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, false, CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (!player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		BlockPlaceContext blockContext = new BlockPlaceContext(context);
		if (!blockContext.canPlace()) {
			return InteractionResult.PASS;
		}
		BlockState blockstate = CABlocks.GOLDEN_CHALISE.get().getStateForPlacement(blockContext);
		if (blockstate == null) {
			return InteractionResult.PASS;
		}
		context.getLevel().setBlock(blockContext.getClickedPos(), blockstate, 3);
		if (!player.getAbilities().instabuild) {
			context.getItemInHand().shrink(1);
		}
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
	}
}
