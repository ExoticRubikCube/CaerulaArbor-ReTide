package com.susen36.caerulaarbor.block;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CABlockEntities;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbandonedSulptureBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final IntegerProperty DATA_ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public AbandonedSulptureBlock() {
        super(BlockBehaviour.Properties.of() .sound(SoundType.DEEPSLATE).strength(16f, 75f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.BLOCK).isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return CABlockEntities.ABANDONED_SULPTURE.get().create(blockPos, blockState);
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
            case NORTH -> box(2, 0, 4, 14, 36, 12);
            case EAST -> box(4, 0, 2, 12, 36, 14);
            case WEST -> box(4, 0, 2, 12, 36, 14);
            default -> box(2, 0, 4, 14, 36, 12);
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
        world.scheduleTick(pos, this, 100);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!(Math.random() > 0.33)) {
            {
                final Vec3 center = new Vec3(((double) x + 0.5), ((double) y + 1), ((double) z + 0.5));
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (!(entityiterator instanceof LivingEntity)) {
                        continue;
                    }
                    if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_boss")))) {
                        continue;
                    }
                    if (new Vec3(((double) x + 0.5), ((double) y + 1), ((double) z + 0.5)).distanceTo(new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()))) < 24) {
                        world.destroyBlock(BlockPos.containing(x, y, z), false);
                        if (world instanceof ServerLevel level) {
                            Entity entityToSpawn = CAEntities.THE_ABANDONED.get().spawn(level, BlockPos.containing((double) x + 0.5, y, (double) z + 0.5), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                            }
                        }
                        break;
                    }
                }
            }
        }
        world.scheduleTick(pos, this, 100);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
        boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (world instanceof ServerLevel level) {
            Entity entityToSpawn = CAEntities.THE_ABANDONED.get().spawn(level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            }
        }
        return retval;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }
}