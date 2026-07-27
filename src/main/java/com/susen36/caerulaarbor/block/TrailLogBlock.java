package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.util.CaerulaUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class TrailLogBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	public static final IntegerProperty GROW_AGE = IntegerProperty.create("grow_age", 0, 64);
	public static final IntegerProperty LONGEVITY = IntegerProperty.create("longevity", 0, 16);
	private static final TagKey<Block> CANNOT_COVER = BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "cannot_cover"));

	public TrailLogBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOD).strength(3f, 5f).speedFactor(0.9f));
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AXIS, GROW_AGE, LONGEVITY);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(AXIS, context.getClickedFace().getAxis()).setValue(GROW_AGE, 0).setValue(LONGEVITY, 16);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		if (rot == Rotation.CLOCKWISE_90 || rot == Rotation.COUNTERCLOCKWISE_90) {
			if (state.getValue(AXIS) == Direction.Axis.X) {
				return state.setValue(AXIS, Direction.Axis.Z);
			} else if (state.getValue(AXIS) == Direction.Axis.Z) {
				return state.setValue(AXIS, Direction.Axis.X);
			}
		}
		return state;
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int growAge = blockstate.getValue(GROW_AGE);
		int longevity = blockstate.getValue(LONGEVITY);
		if (growAge < 64) {
			world.setBlock(pos, blockstate.setValue(GROW_AGE, growAge + 1), 3);
		}
		if (growAge > 30 && growAge < 64 && longevity > 0) {
			for (Direction direction : Direction.values()) {
				if (random.nextFloat() < 0.2F) {
					int spreadLongevity = random.nextFloat() < 0.5F ? longevity - 1 : longevity;
					BlockPos targetPos = pos.relative(direction);
					BlockState targetBlock = world.getBlockState(targetPos);
					if (targetBlock.is(BlockTags.LOGS) && !targetBlock.is(CANNOT_COVER)) {
						world.setBlock(targetPos, CABlocks.TRAIL_LOG.get().withPropertiesOf(targetBlock).setValue(LONGEVITY, spreadLongevity), 3);
					} else if (targetBlock.is(BlockTags.LEAVES) && targetBlock.getBlock() != CABlocks.TRAIL_LEAVE.get()) {
						world.setBlock(targetPos, CABlocks.TRAIL_LEAVE.get().withPropertiesOf(targetBlock), 3);
					} else if (targetBlock.getBlock() == Blocks.VINE) {
						world.destroyBlock(targetPos, false);
					}
				}
			}
		}
		world.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		CaerulaUtil.pokeSlightly(world, pos.getX(), pos.getY(), pos.getZ(), entity);
		return retval;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (entity.getMainHandItem().is(ItemTags.create(ResourceLocation.parse("minecraft:axes"))) || ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.parse("minecraft:axes")))) {
            world.levelEvent(2001, BlockPos.containing(x, y, z), getId(CABlocks.TRAIL_LOG.get().defaultBlockState()));
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1, 1);
            }
            BlockPos bp = BlockPos.containing(x, y, z);
            BlockState bs = CABlocks.STRIPPED_TRAIL_LOG.get().withPropertiesOf(blockstate);
            world.setBlock(bp, bs, 3);
            result = ItemInteractionResult.SUCCESS;
        }
        return result;
	}
}