package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.manager.spwan.SeabornSpawnManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OceanOvaryBlock extends AbstractOvaryBlock {

	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public OceanOvaryBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SCULK_SENSOR).strength(0.5f, 0.5f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				return 4;
			}
		}.getLightLevel())).requiresCorrectToolForDrops().speedFactor(0.9f).jumpFactor(0.9f).noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
				.isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OUTPUT, 0).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected int getTickDelay() {
		return 40;
	}

	@Override
	protected String getDescriptionKey() {
		return "block.caerula_arbor.ocean_ovary.description_0";
	}

	@Override
	protected double getDestroySpawnRate() {
		return 0.05;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {
			return BASE_SHAPE;
		}
		return FULL_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, OUTPUT, POWERED, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(POWERED, false);
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
			world.setBlock(pos, world.getBlockState(pos).setValue(POWERED, true).setValue(OUTPUT, 0).setValue(BLOCKSTATE, 0), 3);
		} else {
			world.setBlock(pos, world.getBlockState(pos).setValue(POWERED, false), 3);
		}
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		if (world.getDifficulty() != Difficulty.PEACEFUL && !(world.getBlockFloorHeight(pos.above()) > 0) && !(world.getBlockFloorHeight(pos.above(2)) > 0)) {
			boolean finished = false;
			if (blockstate.getValue(BLOCKSTATE) == 0) {
				double strategyBreed = MapVariables.get(world).strategy_breed;
				double rate = 0.05D * (1.0D + 0.075D * strategyBreed);
				int output = blockstate.getValue(OUTPUT);
				if (random.nextFloat() < output * 0.005F) {
					double cloneLimit = Math.min(CAConfigs.CLONE_NUM.get(), world.getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT));
					if (EntityUtils.getSeabornNum(world, pos.getX(), pos.getY(), pos.getZ()) >= cloneLimit) {
						finished = true;
					} else {
						SeabornSpawnManager.summonRandomSeaborn(world, rate, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);
						BlockState nextState = blockstate.setValue(OUTPUT, 0);
						if (!blockstate.getValue(POWERED)) {
							nextState = nextState.setValue(BLOCKSTATE, 1);
						}
						world.setBlock(pos, nextState, 3);
					}
				} else if (output < 200) {
					world.setBlock(pos, blockstate.setValue(OUTPUT, output + 1), 3);
				}
			}
			if (!finished && blockstate.getValue(POWERED)) {
				world.setBlock(pos, world.getBlockState(pos).setValue(BLOCKSTATE, 0), 3);
			}
		}
		world.scheduleTick(pos, this, 40);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack itemstack, BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.useItemOn(itemstack, blockstate, world, pos, entity, hand, hit);
		ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        ItemStack fed = ItemStack.EMPTY;
        if (entity.getMainHandItem().is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "fish_food")))) {
            fed = entity.getMainHandItem().copy();
        } else {
            if (entity.getOffhandItem().is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "fish_food")))) {
                fed = entity.getOffhandItem().copy();
            }
        }
        if (fed.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "fish_food")))) {
            if (blockstate.getValue(BLOCKSTATE) == 1) {
                world.setBlock(pos, world.getBlockState(pos).setValue(BLOCKSTATE, 0).setValue(OUTPUT, 0), 3);
                world.playSound(null, pos, SoundEvents.PANDA_EAT, SoundSource.BLOCKS, 0.95F, 1.0F);
                fed.shrink(1);
                result = ItemInteractionResult.SUCCESS;
            }
        }
        return result;
	}
}