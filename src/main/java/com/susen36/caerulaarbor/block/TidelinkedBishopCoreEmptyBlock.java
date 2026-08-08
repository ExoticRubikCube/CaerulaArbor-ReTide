package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

public class TidelinkedBishopCoreEmptyBlock extends Block {
	public TidelinkedBishopCoreEmptyBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.LODESTONE).strength(-1, 3600000).lightLevel(s -> 8).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        Player player;
        ItemStack itemStack = entity.getMainHandItem();
        if (itemStack.getItem() == CAItems.REPELLER_SHELL.get()) {
            Player player2;
            if (world.getBlockState(BlockPos.containing(x, (double) y + 2.0, z)).canBeReplaced()) {
                ItemStack itemStack2;
                world.setBlock(BlockPos.containing(x, (double) y + 2.0, z), CABlocks.TIDELINKED_BISHOP_CORE.get().defaultBlockState(), 3);
                world.setBlock(BlockPos.containing(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
                if ((Entity) entity instanceof LivingEntity livEnt) {
                    itemStack2 = livEnt.getMainHandItem();
                } else {
                    itemStack2 = ItemStack.EMPTY;
                }
                itemStack2.shrink(1);
                if (world instanceof Level level) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 2.0f, 1.0f);
                    } else {
                        level.playLocalSound(x, y, z, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 2.0f, 1.0f, false);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (entity instanceof Player && !(player2 = entity).level().isClientSide()) {
                player2.displayClientMessage(Component.literal(Component.translatable("block.caerula_arbor.tidelinked_bishop_core_empty.warn").getString()), true);
            }
        } else if (entity instanceof Player && !(player = entity).level().isClientSide()) {
            player.displayClientMessage(Component.literal(Component.translatable("block.caerula_arbor.tidelinked_bishop_core_empty.note").getString()), true);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}