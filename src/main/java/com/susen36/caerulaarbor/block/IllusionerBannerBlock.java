
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class IllusionerBannerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 2);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public IllusionerBannerBlock() {
		super(BlockBehaviour.Properties.of()

				.sound(SoundType.WOOD).strength(-1, 3600000).lightLevel(s -> (new Object() {
					public int getLightLevel() {
						if (s.getValue(BLOCKSTATE) == 1)
							return 0;
						return 0;
					}
				}.getLightLevel())).noCollission().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.ILLUSIONER_BANNER.get().create(blockPos, blockState);
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
		if (state.getValue(BLOCKSTATE) == 1) {

			return switch (state.getValue(FACING)) {
                case NORTH -> box(4, 0, 4, 12, 4, 12);
				case EAST -> box(4, 0, 4, 12, 4, 12);
				case WEST -> box(4, 0, 4, 12, 4, 12);
                default -> box(4, 0, 4, 12, 4, 12);
            };
		}

		return switch (state.getValue(FACING)) {
            case NORTH -> box(4, 0, 4, 12, 44, 12);
			case EAST -> box(4, 0, 4, 12, 44, 12);
			case WEST -> box(4, 0, 4, 12, 44, 12);
            default -> box(4, 0, 4, 12, 44, 12);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DATA_ANIMATION, FACING, WATERLOGGED, BLOCKSTATE);
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
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        InteractionResult result = InteractionResult.PASS;
        if (entity != null) {
            double state;
            state = blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1;
            if (state == 0) {
                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.WHIRL_EYE.get()) {
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.OCEANIZED_ILLUSIONER.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                        }
                    }
                    ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                    {
                        int value = 1;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                    }
                    {
                        int value = 1;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = ((LevelAccessor) world).getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(integerProp, value), 3);
                    }
                    result = InteractionResult.SUCCESS;
                } else if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                        && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                        player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.illusioner_banner.note").getString())), true);
                    result = InteractionResult.SUCCESS;
                }
            }
        }
        return result;
	}
}
