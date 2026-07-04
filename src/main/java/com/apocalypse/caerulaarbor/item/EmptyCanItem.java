
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

public class EmptyCanItem extends Item {
	public EmptyCanItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        BlockState target = (((LevelAccessor) world).getFluidState(BlockPos.containing(x + entity.getLookAngle().x, y + entity.getLookAngle().y + 1.6, z + entity.getLookAngle().z)).createLegacyBlock());
        if (Blocks.WATER == target.getBlock()) {
            if ((Entity) entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CAItems.CANNED_WATER.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            itemstack.shrink(1);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
            }
        } else if (Blocks.LAVA == target.getBlock()) {
            if ((Entity) entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CAItems.CANNED_LAVA.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            itemstack.shrink(1);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
            }
        }
        return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        BlockState target;
        target = (world.getFluidState(BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ())).createLegacyBlock());
        if (Blocks.WATER == target.getBlock()) {
            itemstack.shrink(1);
            if (entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CAItems.CANNED_WATER.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
            }
            return InteractionResult.SUCCESS;
        } else if (Blocks.LAVA == target.getBlock()) {
            itemstack.shrink(1);
            if (entity instanceof Player _player) {
                ItemStack _setstack = new ItemStack(CAItems.CANNED_LAVA.get()).copy();
                _setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
