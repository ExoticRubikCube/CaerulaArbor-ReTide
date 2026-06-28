package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

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
            BlockState target = Blocks.AIR.defaultBlockState();
            double dx = 0;
            double dz = 0;
            double dy = 0;
            for (int index0 = 0; index0 < 60; index0++) {
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.SMALL_FLAME, (x + 0.5 + 24 * Math.sin(Math.toRadians(index0 * 3))), (y + 0.5), (x + 0.5 + 24 * Math.cos(Math.toRadians(index0 * 3))), 4, 0.5, 2, 0.5, 0.1);
            }
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 6, 4, 4, 4, 0.1);
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 64, 4, 4, 4, 0.1);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.BLOCKS, (float) 3.2, 1);
            }
            dx = -24;
            for (int index1 = 0; index1 < 49; index1++) {
                dz = -24;
                for (int index2 = 0; index2 < 49; index2++) {
                    dy = -7;
                    for (int index3 = 0; index3 < 15; index3++) {
                        target = (((LevelAccessor) world).getBlockState(BlockPos.containing(x + dx, y + dy, z + dz)));
                        if (new Vec3(dx, dy, dz).distanceTo(new Vec3(0, 0, 0)) <= 24) {
                            if (target.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "blow_up")))) {
                                world.destroyBlock(BlockPos.containing(x + dx, y + dy, z + dz), false);
                            }
                            dy = dy + 1;
                        }
                    }
                    dz = dz + 1;
                }
                dx = dx + 1;
            }
            ((LevelAccessor) world).setBlock(BlockPos.containing(x, y, z), Blocks.IRON_BLOCK.defaultBlockState(), 3);
            {
                final Vec3 _center = new Vec3((x + 0.5), (y + 0.5), (z + 0.5));
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (new Vec3((x + 0.5), (y + 0.5), (z + 0.5)).distanceTo(new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()))) <= 24) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                                && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet")))) {
                            entityiterator.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "brand_bomb")))),
                                    (float) Math.min(48, Math.max((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.1, 8)));
                        }
                    }
                }
            }
        }
	}
}
