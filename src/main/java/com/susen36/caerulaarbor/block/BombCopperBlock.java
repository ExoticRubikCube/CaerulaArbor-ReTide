package com.susen36.caerulaarbor.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CADamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class BombCopperBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BombCopperBlock() {
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
            BlockState target;
            double dx;
            double dz;
            double dy;
            for (int index0 = 0; index0 < 60; index0++) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.SMALL_FLAME, (x + 0.5 + 24 * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (x + 0.5 + 24 * Math.cos(Math.toRadians(index0 * 3))), 4, 0.5, 2, 0.5, 0.1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 6, 4, 4, 4, 0.1);
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 64, 4, 4, 4, 0.1);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, (float) 3.2, 1);
            }
            dx = -24;
            for (int index1 = 0; index1 < 49; index1++) {
                dz = -24;
                for (int index2 = 0; index2 < 49; index2++) {
                    dy = -7;
                    for (int index3 = 0; index3 < 15; index3++) {
                        target = (world.getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                        if (new Vec3(dx, dy, dz).distanceTo(new Vec3(0, 0, 0)) <= 24) {
                            if (target.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "blow_up")))) {
                                world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
                            }
                            dy = dy + 1;
                        }
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
            world.setBlock(BlockPos.containing(x, y, z), Blocks.IRON_BLOCK.defaultBlockState(), 3);
            {
                final Vec3 center = new Vec3((x + 0.5), (y + 0.5), (z + 0.5));
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (new Vec3((x + 0.5), (y + 0.5), (z + 0.5)).distanceTo(new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()))) <= 24) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))
                                && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born_pet")))) {
                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.BRAND_BOMB),
                                    (float) Math.clamp((entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.1, 8, 48));
                        }
                    }
                }
            }
        }
	}
}