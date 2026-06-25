package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
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
		CaerulaArborModEntities.COLLECTOR_PROKARYOTE,
		CaerulaArborModEntities.FLOATER_PROKARYOTE,
		CaerulaArborModEntities.DEPOSITER_PROKARYOTE,
		CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE,
		CaerulaArborModEntities.FEEDER_PROKARYOTE,
		CaerulaArborModEntities.BONE_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_NORMAL_POOL = {
		CaerulaArborModEntities.CHISELER_FISH,
		CaerulaArborModEntities.FLY_FISH,
		CaerulaArborModEntities.PREDATOR_ABYSSAL,
		CaerulaArborModEntities.FAKE_OFFSPRING,
		CaerulaArborModEntities.SLIDER_FISH,
		CaerulaArborModEntities.RUN_FISH,
		CaerulaArborModEntities.SHOOTER_FISH,
		CaerulaArborModEntities.SPLASHER_ABYSSAL
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ANIMAL_POOL = {
		CaerulaArborModEntities.OCEANIZED_PIG,
		CaerulaArborModEntities.OCEANIZED_COW,
		CaerulaArborModEntities.OCEANIZED_SHEEP,
		CaerulaArborModEntities.OCEANIZED_HORSE,
		CaerulaArborModEntities.OCEANIZED_WOLF,
		CaerulaArborModEntities.OCEANIZED_SPIDER,
		CaerulaArborModEntities.OCEANIZED_VILLAGER,
		CaerulaArborModEntities.OCEANIZED_WITCH,
		CaerulaArborModEntities.OCEANIZED_FOX,
		CaerulaArborModEntities.OCEANIZED_POLAR_BEAR,
		CaerulaArborModEntities.OCEANIZE_RABBIT,
		CaerulaArborModEntities.OCEANIZED_CAT,
		CaerulaArborModEntities.OCEANIZED_CHICKEN
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