
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ThirstCoralBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ThirstCoralBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.CORAL_BLOCK).strength(32f, 256f).noCollission().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	public @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return 0xFF7A89EA;
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
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(2, 0, 2, 14, 15, 14);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(WATERLOGGED, flag);
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
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        ItemInteractionResult result = ItemInteractionResult.SUCCESS;
        if (entity == null) {
            result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            double state = 0;
            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.WHIRL_EYE.get()) {
                if (world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.THIRSTER.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                    }
                }
                ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal((Component.translatable("spawn.thirster").getString())), false);
                world.destroyBlock(BlockPos.containing(x, y, z), false);
            } else if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                    && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()) {
                if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                    player.displayClientMessage(Component.literal((Component.translatable("block.caerula_arbor.thirst_coral.note").getString())), true);
            } else {
                result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }
        return result;
	}
}