package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnchorMediumBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
	public static final IntegerProperty DETECT_Y = IntegerProperty.create("detect_y", 0, 43);

	public AnchorMediumBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.NETHERITE_BLOCK).strength(12f, 300f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 8;
				return 8;
			}
		}.getLightLevel())).pushReaction(PushReaction.BLOCK));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVATED, false).setValue(DETECT_Y, 0));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(BLOCKSTATE) == 1) {
			return switch (state.getValue(FACING)) {
                case NORTH -> box(0, 0, 0, 16, 16, 16);
				case EAST -> box(0, 0, 0, 16, 16, 16);
				case WEST -> box(0, 0, 0, 16, 16, 16);
                default -> box(0, 0, 0, 16, 16, 16);
            };
		}
		return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 0, 16, 16, 16);
			case EAST -> box(0, 0, 0, 16, 16, 16);
			case WEST -> box(0, 0, 0, 16, 16, 16);
            default -> box(0, 0, 0, 16, 16, 16);
        };
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, ACTIVATED, DETECT_Y, BLOCKSTATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(ACTIVATED, false).setValue(DETECT_Y, 0);
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
		world.scheduleTick(pos, this, 5);
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		if (world.getBestNeighborSignal(pos) > 0) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            if (!(blockstate.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty getbp1 && blockstate.getValue(getbp1))) {
                if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == CABlocks.ANCHOR_UPPER.get() && (new Object() {
                    public Direction getDirection(BlockPos pos1) {
                        BlockState bs = world.getBlockState(pos1);
                        Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && bs.getValue(property) instanceof Direction dir)
                            return dir;
                        else if (bs.hasProperty(BlockStateProperties.AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                        else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                        return Direction.NORTH;
                    }
                }.getDirection(BlockPos.containing(x, y + 1, z))) == (new Object() {
                    public Direction getDirection(BlockState bs) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (prop instanceof DirectionProperty dp)
                            return bs.getValue(dp);
                        prop = bs.getBlock().getStateDefinition().getProperty("axis");
                        return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                    }
                }.getDirection(blockstate)) && (world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == CABlocks.ANCHOR_LOWER.get() && (new Object() {
                    public Direction getDirection(BlockPos pos1) {
                        BlockState bs = world.getBlockState(pos1);
                        Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && bs.getValue(property) instanceof Direction dir)
                            return dir;
                        else if (bs.hasProperty(BlockStateProperties.AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                        else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                        return Direction.NORTH;
                    }
                }.getDirection(BlockPos.containing(x, y - 1, z))) == (new Object() {
                    public Direction getDirection(BlockState bs) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (prop instanceof DirectionProperty dp)
                            return bs.getValue(dp);
                        prop = bs.getBlock().getStateDefinition().getProperty("axis");
                        return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                    }
                }.getDirection(blockstate))) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 2, 1);
                    }
                    {
                        int value = 1;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
                    }
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty booleanProp)
                            world.setBlock(blockPos, bs.setValue(booleanProp, true), 3);
                    }
                }
            }
        }
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        double dx;
        double dy;
        double dz;
        double attr;
        BlockState target;
        if (blockstate.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty getbp1 && blockstate.getValue(getbp1)) {
            {
                int value = 1;
                BlockPos blockPos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(blockPos);
                if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                    world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
            }
            if (!((world.getBlockState(BlockPos.containing(x, (double) y + 1, z))).getBlock() == CABlocks.ANCHOR_UPPER.get() && (new Object() {
                public Direction getDirection(BlockPos pos1) {
                    BlockState bs = world.getBlockState(pos1);
                    Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (property != null && bs.getValue(property) instanceof Direction dir)
                        return dir;
                    else if (bs.hasProperty(BlockStateProperties.AXIS))
                        return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                    else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                        return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                    return Direction.NORTH;
                }
            }.getDirection(BlockPos.containing(x, (double) y + 1, z))) == (new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)) && (world.getBlockState(BlockPos.containing(x, (double) y - 1, z))).getBlock() == CABlocks.ANCHOR_LOWER.get() && (new Object() {
                public Direction getDirection(BlockPos pos1) {
                    BlockState bs = world.getBlockState(pos1);
                    Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (property != null && bs.getValue(property) instanceof Direction dir)
                        return dir;
                    else if (bs.hasProperty(BlockStateProperties.AXIS))
                        return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                    else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                        return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                    return Direction.NORTH;
                }
            }.getDirection(BlockPos.containing(x, (double) y - 1, z))) == (new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
                }
            }.getDirection(blockstate)))) {
                {
                    int value = 0;
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = world.getBlockState(blockPos);
                    if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                        world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
                }
                {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState bs = world.getBlockState(blockPos);
                    if (bs.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty booleanProp)
                        world.setBlock(blockPos, bs.setValue(booleanProp, false), 3);
                }
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CONDUIT_DEACTIVATE, SoundSource.NEUTRAL, 2, 1);
                }
            }
            for (int index0 = 0; index0 < 24; index0++) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.NAUTILUS, ((double) x + Mth.nextDouble(RandomSource.create(), -36, 37)), y, ((double) z + -36), 4, 0.5, 4, 0.5, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.NAUTILUS, ((double) x + Mth.nextDouble(RandomSource.create(), -36, 37)), y, ((double) z + 37), 4, 0.5, 4, 0.5, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.NAUTILUS, ((double) x + -36), y, ((double) z + Mth.nextDouble(RandomSource.create(), -36, 25)), 4, 0.5, 4, 0.5, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.NAUTILUS, ((double) x + 37), y, ((double) z + Mth.nextDouble(RandomSource.create(), -36, 37)), 4, 0.5, 4, 0.5, 0.1);
            }
            dy = -21 + (blockstate.getBlock().getStateDefinition().getProperty("detect_y") instanceof IntegerProperty getip27 ? blockstate.getValue(getip27) : -1);
            dx = -36;
            for (int index1 = 0; index1 < 73; index1++) {
                dz = -36;
                for (int index2 = 0; index2 < 73; index2++) {
                    target = (world.getBlockState(BlockPos.containing((double) x + dx, (double) y + dy, (double) z + dz)));
                    if (target.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "trail")))) {
                        attr = target.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty getip30 ? target.getValue(getip30) : -1;
                        if (attr < 61) {
                            for (int index3 = 0; index3 < 11; index3++) {
                                attr = Math.max(0, attr - 4);
                            }
                            {
                                int value = (int) attr;
                                BlockPos blockPos = BlockPos.containing((double) x + dx, (double) y + dy, (double) z + dz);
                                BlockState bs = world.getBlockState(blockPos);
                                if (bs.getBlock().getStateDefinition().getProperty("grow_age") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                                    world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
                            }
                        }
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
            if (dy >= 22) {
                dy = -22;
            }
            {
                int value = (int) (dy + 22);
                BlockPos blockPos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(blockPos);
                if (bs.getBlock().getStateDefinition().getProperty("detect_y") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                    world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
            }
            for (Entity entityiterator : world.getEntities(null, new AABB(((double) x + 37), ((double) y + 22), ((double) z + 37), ((double) x - 36), ((double) y - 21), ((double) z - 36)))) {
                if (!(entityiterator instanceof LivingEntity livEnt33 && livEnt33.hasEffect(CAMobEffects.POWER_OF_ANCHOR))) {
                    if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                        livingEntity.addEffect(new MobEffectInstance(CAMobEffects.POWER_OF_ANCHOR, 20, 0, false, false));
                }
            }
        } else {
            {
                int value = 0;
                BlockPos blockPos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(blockPos);
                if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                    world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
            }
        }
        world.scheduleTick(pos, this, 5);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
		super.useWithoutItem(blockstate, world, pos, entity, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
        InteractionResult result = InteractionResult.SUCCESS;
        boolean finished = false;
        if (direction == null || entity == null) {
            result = InteractionResult.PASS;
        } else {
            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
                    && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem() && direction == (new Object() {
                public Direction getDirection(BlockState bs) {
                    Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                    if (prop instanceof DirectionProperty dp)
                        return bs.getValue(dp);
                    prop = bs.getBlock().getStateDefinition().getProperty("axis");
                    return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis
                            ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE)
                            : Direction.NORTH;
                }
            }.getDirection(blockstate))) {
                if (blockstate.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty getbp5 && blockstate.getValue(getbp5)) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CONDUIT_DEACTIVATE, SoundSource.NEUTRAL, 2, 1);
                    }
                    {
                        int value = 0;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
                    }
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty booleanProp)
                            world.setBlock(blockPos, bs.setValue(booleanProp, false), 3);
                    }
                    finished = true;
                } else if ((world.getBlockState(BlockPos.containing(x, (double) y + 1, z))).getBlock() == CABlocks.ANCHOR_UPPER.get() && (new Object() {
                    public Direction getDirection(BlockPos pos1) {
                        BlockState bs = world.getBlockState(pos1);
                        Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && bs.getValue(property) instanceof Direction dir)
                            return dir;
                        else if (bs.hasProperty(BlockStateProperties.AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                        else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                        return Direction.NORTH;
                    }
                }.getDirection(BlockPos.containing(x, (double) y + 1, z))) == (new Object() {
                    public Direction getDirection(BlockState bs) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (prop instanceof DirectionProperty dp)
                            return bs.getValue(dp);
                        prop = bs.getBlock().getStateDefinition().getProperty("axis");
                        return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis
                                ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE)
                                : Direction.NORTH;
                    }
                }.getDirection(blockstate)) && (world.getBlockState(BlockPos.containing(x, (double) y - 1, z))).getBlock() == CABlocks.ANCHOR_LOWER.get() && (new Object() {
                    public Direction getDirection(BlockPos pos1) {
                        BlockState bs = world.getBlockState(pos1);
                        Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (property != null && bs.getValue(property) instanceof Direction dir)
                            return dir;
                        else if (bs.hasProperty(BlockStateProperties.AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
                        else if (bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                            return Direction.fromAxisAndDirection(bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
                        return Direction.NORTH;
                    }
                }.getDirection(BlockPos.containing(x, (double) y - 1, z))) == (new Object() {
                    public Direction getDirection(BlockState bs) {
                        Property<?> prop = bs.getBlock().getStateDefinition().getProperty("facing");
                        if (prop instanceof DirectionProperty dp)
                            return bs.getValue(dp);
                        prop = bs.getBlock().getStateDefinition().getProperty("axis");
                        return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis
                                ? Direction.fromAxisAndDirection((Direction.Axis) bs.getValue(ep), Direction.AxisDirection.POSITIVE)
                                : Direction.NORTH;
                    }
                }.getDirection(blockstate))) {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CONDUIT_ACTIVATE, SoundSource.NEUTRAL, 2, 1);
                    }
                    {
                        int value = 1;
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
                            world.setBlock(blockPos, bs.setValue(integerProp, value), 3);
                    }
                    {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(blockPos);
                        if (bs.getBlock().getStateDefinition().getProperty("activated") instanceof BooleanProperty booleanProp)
                            world.setBlock(blockPos, bs.setValue(booleanProp, true), 3);
                    }
                    finished = true;
                } else {
                    result = InteractionResult.FAIL;
                    finished = true;
                }
            }
            if (!finished) {
                result = InteractionResult.PASS;
            }
        }
        return result;
	}
}