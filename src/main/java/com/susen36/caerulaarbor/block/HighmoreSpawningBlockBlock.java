
package com.susen36.caerulaarbor.block;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

public class HighmoreSpawningBlockBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 2);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public HighmoreSpawningBlockBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.BASALT).strength(-1, 3600000).lightLevel(s -> (new Object() {
					public int getLightLevel() {
						if (s.getValue(BLOCKSTATE) == 1)
							return 1;
						return 6;
					}
				}.getLightLevel())).noOcclusion().pushReaction(PushReaction.BLOCK).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return CABlockEntities.HIGHMORE_SPAWNING_BLOCK.get().create(blockPos, blockState);
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

			return box(0, 0, 0, 16, 12, 16);
		}

		return box(0, 0, 0, 16, 16, 16);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DATA_ANIMATION, WATERLOGGED, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(WATERLOGGED, flag);
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
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1) == 0) {
            {
                int value = 1;
                BlockPos blockPos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(pos);
                if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                    world.setBlock(pos, bs.setValue(integerProp, value), 3);
            }
            CaerulaArborMod.queueServerWork(40, () -> {
                if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == CABlocks.HIGHMORE_SPAWNING_BLOCK.get()
                        && ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip6
                                ? (world.getBlockState(BlockPos.containing(x, y, z))).getValue(getip6)
                                : -1) == 0) {
                    {
                        int value = 1;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(pos, bs.setValue(integerProp, value), 3);
                    }
                    {
                        int value = 0;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(pos, bs.setValue(integerProp, value), 3);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        Entity entityToSpawn = CAEntities.HIGHMORE.get().spawn(level, BlockPos.containing(x + 0.5, y + 1, z + 0.5), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                }
            });
        }
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
        ItemInteractionResult result = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (entity != null) {
            if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty getip1 ? blockstate.getValue(getip1) : -1) == 1) {
                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CAItems.HIGHMORE_SCYTHE.get()) {
                    world.setBlock(BlockPos.containing(x, y, z), CABlocks.HIGHMORE_SPAWNBLOCK.get().defaultBlockState(), 3);
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, (float) 1.5, 1);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.WAX_ON, ((double) x + 0.5), ((double) y + 0.5), ((double) z + 0.5), 16, 2, 2, 2, 0.15);
                    for (int index0 = 0; index0 < 16; index0++) {
                        {
                            ItemStack ist = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY);
                            if (ist.getDamageValue() + 64 >= ist.getMaxDamage()) {
                                ist.shrink(1);
                                ist.setDamageValue(0);
                            } else {
                                ist.setDamageValue(ist.getDamageValue() + 64);
                            }
                        }
                    }
                    result = ItemInteractionResult.SUCCESS;
                }
            }
        }
        return result;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return MapCodec.unit(this);
	}
}