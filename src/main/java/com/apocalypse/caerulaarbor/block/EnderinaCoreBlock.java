package com.apocalypse.caerulaarbor.block;

import com.apocalypse.caerulaarbor.entity.MoistEnderCrystalEntity;
import com.apocalypse.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EnderinaCoreBlock extends Block {
	public static final BooleanProperty CAN_SUMMON = BlockStateProperties.CAN_SUMMON;

	public EnderinaCoreBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.METAL).strength(8f, 256f).lightLevel(s -> 12).requiresCorrectToolForDrops().pushReaction(PushReaction.BLOCK));
		this.registerDefaultState(this.stateDefinition.any().setValue(CAN_SUMMON, false));
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return new float[]{0.7254901961f, 0.2509803922f, 0.9058823529f};
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CAN_SUMMON);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(CAN_SUMMON, false);
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		world.scheduleTick(pos, this, 40);
	}

	@Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
        boolean canSummon = false;
        double count = 0;
        BlockPos curPos = BlockPos.containing(x, y, z);
        if (!(blockstate.getBlock().getStateDefinition().getProperty("can_summon") instanceof BooleanProperty getbp1 && blockstate.getValue(getbp1))) {
            if(world.getEntitiesOfClass(OceanizedEnderinaEntity.class,
                    AABB.ofSize(new Vec3(x, y, z), 48, 48, 48), e -> e.isAlive()).isEmpty()) {
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<MoistEnderCrystalEntity> entfound = world.getEntitiesOfClass(MoistEnderCrystalEntity.class,
                            new AABB(center, center).inflate(11 / 2d), e -> {
                                return e.IS_STATIC;
                            });
                    for (MoistEnderCrystalEntity entityiterator : entfound) {
                        count++;
                        OceanizedEnderinaEntity.spawnLinkParticles(world, entityiterator.getX(), entityiterator.getY() + 1, entityiterator.getZ(), (double) x + 0.5, (double) y + 1, (double) z + 0.5);
                    }
                }
                if (count >= 4) {
                    {
                        BlockPos blockPos = curPos;
                        BlockState bs = blockstate;
                        if (bs.getBlock().getStateDefinition().getProperty("can_summon") instanceof BooleanProperty booleanProp)
                            ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, true), 3);
                    }
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, curPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.BLOCKS, 2, 1);
                    }
                }
            }
        } else {
            {
                final Vec3 center = new Vec3(x, y, z);
                List<MoistEnderCrystalEntity> entfound = world.getEntitiesOfClass(MoistEnderCrystalEntity.class,
                new AABB(center, center).inflate(11 / 2d), e -> {return e.IS_STATIC;});
                for (MoistEnderCrystalEntity entityiterator : entfound) {
                    if (!entityiterator.level().isClientSide())
                        entityiterator.discard();
                    count = count + 1;
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.EXPLOSION, (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 2, 0.1, 0.1, 0.1, 1);
                    if (count >= 4) {
                        break;
                    }
                }
            }
            if (count >= 4) {
                if ((LevelAccessor) world instanceof ServerLevel level) {
                    LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(level);
                    entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
                    entityToSpawn.setVisualOnly(true);
                    level.addFreshEntity(entityToSpawn);
                }
                if ((LevelAccessor) world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.OCEANIZED_ENDERINA.get().spawn(level, BlockPos.containing(x, (double) y + 1, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                    }
                }
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, curPos, CASounds.CASTER_SKILL.get(), SoundSource.BLOCKS, 2, 1);
                }
                world.destroyBlock(curPos, false);
            } else {
                {
                    BlockPos blockPos = curPos;
                    BlockState bs = blockstate;
                    if (bs.getBlock().getStateDefinition().getProperty("can_summon") instanceof BooleanProperty booleanProp)
                        ((LevelAccessor) world).setBlock(pos, bs.setValue(booleanProp, false), 3);
                }
            }
        }
        world.scheduleTick(pos, this, 40);
	}
}
