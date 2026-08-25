
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class FaxBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public FaxBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1f, 12f).requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		if (!(world instanceof ServerLevel level)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		Direction direction = hit.getDirection();
		double spawnX = pos.getX() + direction.getStepX() + 0.5;
		double spawnY = pos.getY() + direction.getStepY();
		double spawnZ = pos.getZ() + direction.getStepZ() + 0.5;
		ItemStack treaty = entity.getMainHandItem();
		boolean isCreative = entity.getAbilities().instabuild;

		int expCost = 0;
		if (treaty.is(CAItems.TREATY_COPPER.get())) {
			expCost = 5;
		} else if (treaty.is(CAItems.TREATY_IRON.get())) {
			expCost = 7;
		} else if (treaty.is(CAItems.TREATY_GOLD.get()) || treaty.is(CAItems.EMERALD_TREATY.get())) {
			expCost = 9;
		} else if (treaty.is(CAItems.TREATY_DIAMOND.get())) {
			expCost = 11;
		}
		if (expCost == 0) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (!isCreative && entity.experienceLevel < expCost) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (!isCreative) {
			entity.giveExperienceLevels(-expCost);
		}
		treaty.shrink(1);

		if (treaty.is(CAItems.TREATY_COPPER.get())) {
			CAEntities.JUNIOR_WARRIOR_PRIEST.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
		} else if (treaty.is(CAItems.TREATY_IRON.get())) {
			CAEntities.WARRIOR_PRIEST.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
		} else if (treaty.is(CAItems.TREATY_GOLD.get())) {
			if (level.getRandom().nextFloat() < 0.5f) {
				CAEntities.CORRECTIONAL_PHALAX_VANGUARD.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
			} else {
				CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
			}
		} else if (treaty.is(CAItems.EMERALD_TREATY.get())) {
			CAEntities.TRIBUNAL_HEALER.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
		} else if (treaty.is(CAItems.TREATY_DIAMOND.get())) {
			level.sendParticles(CAParticles.PURPLE_FLAME.get(), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 32, 0.75, 0.75, 0.75, 0.1);
			CAEntities.IRENE.get().spawn(level, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
		}
		return ItemInteractionResult.SUCCESS;
	}
}