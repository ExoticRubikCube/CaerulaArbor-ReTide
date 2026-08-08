package com.susen36.caerulaarbor.manager.spwan;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SeabornSpawnManager {
	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] WATER_NORMAL_POOL = {
		CAEntities.COLLECTOR_PROKARYOTE,
		CAEntities.FLOATER_PROKARYOTE,
		CAEntities.DEPOSITER_PROKARYOTE,
		CAEntities.ACCUMULATOR_PROKARYOTE,
		CAEntities.FEEDER_PROKARYOTE,
		CAEntities.BONE_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] LAND_NORMAL_POOL = {
		CAEntities.OCEAN_STONECUTTE,
		CAEntities.FLY_FISH,
		CAEntities.PREDATOR_ABYSSAL,
		CAEntities.FAKE_OFFSPRING,
		CAEntities.SLIDER_FISH,
		CAEntities.RUN_FISH,
		CAEntities.SHOOTER_FISH,
		CAEntities.SPLASHER_ABYSSAL
	};

	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] OCEANIZED_ANIMAL_POOL = {
		CAEntities.OCEANIZED_PIG,
		CAEntities.OCEANIZED_COW,
		CAEntities.OCEANIZED_SHEEP,
		CAEntities.OCEANIZED_HORSE,
		CAEntities.OCEANIZED_WOLF,
		CAEntities.OCEANIZED_SPIDER,
		CAEntities.OCEANIZED_VILLAGER,
		CAEntities.OCEANIZED_WITCH,
		CAEntities.OCEANIZED_FOX,
		CAEntities.OCEANIZED_POLAR_BEAR,
		CAEntities.OCEANIZE_RABBIT,
		CAEntities.OCEANIZED_CAT,
		CAEntities.OCEANIZED_CHICKEN
	};

	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] WATER_ELITE_POOL = {
		CAEntities.APOSTLE_PROKARYOTE,
		CAEntities.NUCLEIC_MALEFICENT
	};

	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] LAND_ELITE_POOL = {
		CAEntities.BASELAYER_ABYSSAL,
		CAEntities.CRACKER_ABYSSAL,
		CAEntities.POCKET_SEA_CREEPER,
		CAEntities.GUIDE_ABYSSAL,
		CAEntities.PUNCTURE_FISH,
		CAEntities.REAPER_FISH,
		CAEntities.UMBRELLA_ABYSSAL,
		CAEntities.PREGNANT_FISH,
		CAEntities.FLEE_FISH,
		CAEntities.CHEST_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final DeferredHolder[] OCEANIZED_ELITE_POOL = {
		CAEntities.OCEANIZED_VINDICATOR,
		CAEntities.OCEANIZED_EVOKER,
		CAEntities.OCEANIZED_RAVAGER,
		CAEntities.OCEANIZED_ENDERMAN,
		CAEntities.COMPASSION_PRAYER
	};

	private SeabornSpawnManager() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void summonRandomSeaborn(LevelAccessor world, double eliteChance, double x, double y, double z) {
		if (Math.random() < eliteChance) {
			summonEliteSeaborn(world, x, y, z);
		} else {
			if ((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER) {
				int rand = Mth.nextInt(RandomSource.create(), 0, WATER_NORMAL_POOL.length - 1);
				if (world instanceof ServerLevel level) {
					Entity entityToSpawn = ((EntityType<?>) WATER_NORMAL_POOL[rand].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand = Mth.nextInt(RandomSource.create(), 0, 8);
				if (rand < LAND_NORMAL_POOL.length) {
					if (world instanceof ServerLevel level) {
						Entity entityToSpawn = ((EntityType<?>) LAND_NORMAL_POOL[rand].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				} else {
					int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ANIMAL_POOL.length - 1);
					if (world instanceof ServerLevel level) {
						Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ANIMAL_POOL[rand1].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				}
			}
		}
	}

	public static void summonEliteSeaborn(LevelAccessor world, double x, double y, double z) {
		if ((world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER) {
			int rand = Mth.nextInt(RandomSource.create(), 0, WATER_ELITE_POOL.length - 1);
			if (world instanceof ServerLevel level) {
				Entity entityToSpawn = ((EntityType<?>) WATER_ELITE_POOL[rand].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else {
			int rand = Mth.nextInt(RandomSource.create(), 0, 8);
			if (Math.random() < 0.04) {
				rand = 9;
			}
			if (rand <= 9) {
				if (world instanceof ServerLevel level) {
					Entity entityToSpawn = ((EntityType<?>) LAND_ELITE_POOL[rand].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ELITE_POOL.length - 1);
				if (world instanceof ServerLevel level) {
					Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ELITE_POOL[rand1].get()).spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.CLOUD, x, y, z, 32, 1, 1, 1, 0.1);
		}
		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PHANTOM_SWOOP, SoundSource.NEUTRAL, 1, 1);
		}
	}
}