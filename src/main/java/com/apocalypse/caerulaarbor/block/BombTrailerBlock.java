package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BombTrailerBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BombTrailerBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(4f, 32f).requiresCorrectToolForDrops());
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
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            double dx;
            double dz;
            double dy;
            BlockState target;
            dx = -10;
            for (int index0 = 0; index0 < 21; index0++) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMALL_FLAME, (x + dx), (y + 0.5), (z + 10), 32, 0.5, 2, 0.5, 0.1);
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMALL_FLAME, (x + dx), (y + 0.5), (z - 10), 32, 0.5, 2, 0.5, 0.1);
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMALL_FLAME, (x + 10), (y + 0.5), (z + dx), 32, 0.5, 2, 0.5, 0.1);
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMALL_FLAME, (x - 10), (y + 0.5), (z + dx), 32, 0.5, 2, 0.5, 0.1);
                dx = dx + 1;
            }
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 6, 3, 3, 3, 0.1);
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 128, 3, 3, 3, 0.1);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, (float) 3.2, 1);
            }
            dx = -10;
            for (int index1 = 0; index1 < 21; index1++) {
                dz = -10;
                for (int index2 = 0; index2 < 21; index2++) {
                    dy = -2;
                    for (int index3 = 0; index3 < 5; index3++) {
                        target = (((LevelAccessor) world).getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                        if (target.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "blow_up")))) {
                            world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
                        }
                        dy = dy + 1;
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
            ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), Blocks.IRON_BLOCK.defaultBlockState(), 3);
        }
	}
}
