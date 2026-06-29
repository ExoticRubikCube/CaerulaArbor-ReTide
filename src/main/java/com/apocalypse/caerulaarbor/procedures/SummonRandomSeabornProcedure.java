package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

public class SummonRandomSeabornProcedure {
	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] WATER_NORMAL_POOL = {
		CAEntities.COLLECTOR_PROKARYOTE,
		CAEntities.FLOATER_PROKARYOTE,
		CAEntities.DEPOSITER_PROKARYOTE,
		CAEntities.ACCUMULATOR_PROKARYOTE,
		CAEntities.FEEDER_PROKARYOTE,
		CAEntities.BONE_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_NORMAL_POOL = {
		CAEntities.CHISELER_FISH,
		CAEntities.FLY_FISH,
		CAEntities.PREDATOR_ABYSSAL,
		CAEntities.FAKE_OFFSPRING,
		CAEntities.SLIDER_FISH,
		CAEntities.RUN_FISH,
		CAEntities.SHOOTER_FISH,
		CAEntities.SPLASHER_ABYSSAL
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ANIMAL_POOL = {
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

	public static void execute(LevelAccessor world, double elite_chan, double xx, double yy, double zz) {
		if (Math.random() < elite_chan) {
			SummonEliteFishProcedure.execute(world, xx, yy, zz);
		} else {
			if ((world.getFluidState(BlockPos.containing(xx, yy, zz)).createLegacyBlock()).getBlock() == Blocks.WATER) {
				int rand = Mth.nextInt(RandomSource.create(), 0, WATER_NORMAL_POOL.length - 1);
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) WATER_NORMAL_POOL[rand].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand = Mth.nextInt(RandomSource.create(), 0, 8);
				if (rand < LAND_NORMAL_POOL.length) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = ((EntityType<?>) LAND_NORMAL_POOL[rand].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				} else {
					int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ANIMAL_POOL.length - 1);
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ANIMAL_POOL[rand1].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
				}
			}
		}
	}
}