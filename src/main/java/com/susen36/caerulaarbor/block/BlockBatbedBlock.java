
package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Comparator;
import java.util.List;

public class BlockBatbedBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BlockBatbedBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(0.5f, 1f).jumpFactor(1.3f).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
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
		return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(box(0, 0, 0, 16, 12, 2), box(0, 0, 2, 2, 12, 16), box(14, 0, 2, 16, 12, 16), box(2, 0, 2, 14, 2, 16), box(2, 2, 2, 14, 5, 16));
			case EAST -> Shapes.or(box(14, 0, 0, 16, 12, 16), box(0, 0, 0, 14, 12, 2), box(0, 0, 14, 14, 12, 16), box(0, 0, 2, 14, 2, 14), box(0, 2, 2, 14, 5, 14));
			case WEST -> Shapes.or(box(0, 0, 0, 2, 12, 16), box(2, 0, 14, 16, 12, 16), box(2, 0, 0, 16, 12, 2), box(2, 0, 2, 16, 2, 14), box(2, 2, 2, 16, 5, 14));
            default -> Shapes.or(box(0, 0, 14, 16, 12, 16), box(14, 0, 0, 16, 12, 14), box(0, 0, 0, 2, 12, 14), box(2, 0, 0, 14, 2, 14), box(2, 2, 0, 14, 5, 14));
        };
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
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		if (worldIn instanceof LevelAccessor world) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
            return (world.getBlockState(BlockPos.containing((double) x + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepX(), y, (double) z + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepZ()))).canBeReplaced() || (world.getBlockState(BlockPos.containing((double) x + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepX(), y, (double) z + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepZ()))).getBlock() == CABlocks.BATBED_UPPER.get();
        }
		return super.canSurvive(blockstate, worldIn, pos);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		return !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 20);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if ((world.getBlockState(BlockPos.containing(x + ((new Object() {
            public Direction getDirection(BlockState bs) {
                Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                if (prop instanceof DirectionProperty dp)
                    return bs.getValue(dp);
                prop = bs.getBlock().getStateDefinition().getProperty("axis");
                return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
            }
        }.getDirection(blockstate)).getOpposite()).getStepX(), y, z + ((new Object() {
            public Direction getDirection(BlockState bs) {
                Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                if (prop instanceof DirectionProperty dp)
                    return bs.getValue(dp);
                prop = bs.getBlock().getStateDefinition().getProperty("axis");
                return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
            }
        }.getDirection(blockstate)).getOpposite()).getStepZ()))).canBeReplaced()) {
            world.setBlock(BlockPos.containing(x + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepX(), y, z + ((new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)).getOpposite()).getStepZ()), (new Object() {
                public BlockState with(BlockState bs, Direction newValue) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp && dp.getPossibleValues().contains(newValue))
                        return bs.setValue(dp, newValue);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().contains(newValue.getAxis()) ? bs.setValue(ep, newValue.getAxis()) : bs;
                }
            }.with(CABlocks.BATBED_UPPER.get().defaultBlockState(), (new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)))), 3);
        }
    }

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (!((world.getBlockState(BlockPos.containing(x + ((new Object() {
            public Direction getDirection(BlockState bs) {
                Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                if (prop instanceof DirectionProperty dp)
                    return bs.getValue(dp);
                prop = bs.getBlock().getStateDefinition().getProperty("axis");
                return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
            }
        }.getDirection((world.getBlockState(BlockPos.containing(x, y, z))))).getOpposite()).getStepX(), y, z + ((new Object() {
            public Direction getDirection(BlockState bs) {
                Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                if (prop instanceof DirectionProperty dp)
                    return bs.getValue(dp);
                prop = bs.getBlock().getStateDefinition().getProperty("axis");
                return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
            }
        }.getDirection((world.getBlockState(BlockPos.containing(x, y, z))))).getOpposite()).getStepZ()))).getBlock() == CABlocks.BATBED_UPPER.get())) {
            {
                BlockPos blockPos = BlockPos.containing(x, y, z);
                dropResources(world.getBlockState(blockPos), world, BlockPos.containing(x, y, z), null);
                world.destroyBlock(blockPos, false);
            }
        }
    }

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        {
            final Vec3 center = new Vec3(x, y, z);
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if ((entityiterator instanceof Bat || entityiterator instanceof LivingEntity livEnt1 && livEnt1.getType().is(EntityTypeTags.UNDEAD))
                        && new Vec3(((double) x + 0.5), ((double) y + 1), ((double) z + 0.5)).distanceTo(new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()))) > 1) {
                    if (Math.random() < 0.2) {
                        if (entityiterator instanceof Mob mob)
                            mob.getNavigation().moveTo(((double) x + Mth.nextDouble(RandomSource.create(), -3, 4)), y, ((double) z + Mth.nextDouble(RandomSource.create(), -3, 4)), 1);
                    }
                }
            }
        }
        world.scheduleTick(pos, this, 20);
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
        return retval;
	}

	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion e) {
		super.wasExploded(world, pos, e);
    }
}