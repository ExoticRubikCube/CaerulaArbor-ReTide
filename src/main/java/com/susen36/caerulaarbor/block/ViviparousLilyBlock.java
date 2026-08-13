
package com.susen36.caerulaarbor.block;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ViviparousLilyBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final net.minecraft.tags.TagKey<Block> TRAIL_TAG = BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "trail"));

	public ViviparousLilyBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.FUNGUS).instabreak().lightLevel(s -> 1).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.VIVIPAROUS_LILY.get().create(blockPos, blockState);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

		return switch (state.getValue(FACING)) {
            case NORTH -> box(6, 0, 6, 10, 5, 10);
			case EAST -> box(6, 0, 6, 10, 5, 10);
			case WEST -> box(6, 0, 6, 10, 5, 10);
            default -> box(6, 0, 6, 10, 5, 10);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DATA_ANIMATION, FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
            return world.getBlockState(BlockPos.containing(x, (double) y - 1, z)).isFaceSturdy(world, BlockPos.containing(x, (double) y - 1, z), Direction.UP);
		}
		return super.canSurvive(blockstate, worldIn, pos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		Direction direction = switch (world.getRandom().nextInt(4)) {
			case 0 -> Direction.EAST;
			case 1 -> Direction.SOUTH;
			case 2 -> Direction.WEST;
			default -> Direction.NORTH;
		};
		world.setBlock(pos, blockstate.setValue(FACING, direction), 3);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		boolean huge = true;
		for (int dx = -1; dx <= 1 && huge; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (world.getBlockState(pos.offset(dx, 0, dz)).getBlock() != CABlocks.VIVIPAROUS_LILY.get()) {
					huge = false;
					break;
				}
			}
		}
		if (huge) {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx != 0 || dz != 0) {
						world.setBlock(pos.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
						world.playSound(null, pos, SoundEvents.FUNGUS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
					}
				}
			}
			world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 2, 0.1D, 0.1D, 0.1D, 0.1D);
			world.setBlock(pos, blockstate.setValue(DATA_ANIMATION, 1), 3);
			CaerulaArbor.queueServerWork(20, () -> {
				if (world.getBlockState(pos).getBlock() == CABlocks.VIVIPAROUS_LILY.get()) {
					world.setBlock(pos, CABlocks.HUGE_LILY.get().withPropertiesOf(blockstate), 3);
				}
			});
		} else if (world.getBlockState(pos.below()).is(TRAIL_TAG) && random.nextFloat() < 0.33F) {
			Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(world, pos.above(), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
			}
			world.playSound(null, pos, SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.BLOCKS, 1.0F, 1.0F);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return MapCodec.unit(this);
	}
}