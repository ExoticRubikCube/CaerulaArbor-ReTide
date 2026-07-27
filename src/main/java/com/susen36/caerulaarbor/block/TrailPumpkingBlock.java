
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class TrailPumpkingBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public TrailPumpkingBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.FROGLIGHT).strength(2f).lightLevel(s -> 6).friction(0.7f).speedFactor(0.9f).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true));
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
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        boolean hor = false;
        boolean vec = false;
        BlockState taregt;
        double direc = 0;
        taregt = CABlocks.CHITIN_BLOCK.get().defaultBlockState();
        if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == taregt.getBlock()) {
            vec = true;
        }
        if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == taregt.getBlock()) {
            hor = true;
            direc = 0;
        } else if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == taregt.getBlock()) {
            hor = true;
            direc = 1;
        }
        if (hor && vec) {
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 2, z), false);
            if (direc == 0) {
                world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
                world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
            } else if (direc == 1) {
                world.destroyBlock(BlockPos.containing(x, y - 1, z - 1), false);
                world.destroyBlock(BlockPos.containing(x, y - 1, z + 1), false);
            }
            if ((LevelAccessor) world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.CHITIN_GOLEM.get().spawn(level, BlockPos.containing(x + 0.5, y - 2, z + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                }
            }
            return;
        }
        taregt = CABlocks.COMPLEX_CHITIN_BLOCK.get().defaultBlockState();
        if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == taregt.getBlock()) {
            vec = true;
        }
        if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == taregt.getBlock()) {
            hor = true;
            direc = 0;
        } else if ((((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == taregt.getBlock() && (((LevelAccessor) world).getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == taregt.getBlock()) {
            hor = true;
            direc = 1;
        }
        if (hor && vec) {
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 2, z), false);
            if (direc == 0) {
                world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
                world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
            } else if (direc == 1) {
                world.destroyBlock(BlockPos.containing(x, y - 1, z - 1), false);
                world.destroyBlock(BlockPos.containing(x, y - 1, z + 1), false);
            }
            if ((LevelAccessor) world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.COMPLEX_CHITIN_GOLEM.get().spawn(level, BlockPos.containing(x + 0.5, y - 2, z + 0.5), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                }
            }
        }
    }
}