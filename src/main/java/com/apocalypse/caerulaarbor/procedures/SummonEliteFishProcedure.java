package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SummonEliteFishProcedure {
	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] WATER_ELITE_POOL = {
		CAEntities.APOSTLE_PROKARYOTE,
		CAEntities.NUCLEIC_MALEFICENT
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_ELITE_POOL = {
		CAEntities.BASELAYER_ABYSSAL,
		CAEntities.CRACKER_ABYSSAL,
		CAEntities.CREEPER_FISH,
		CAEntities.GUIDE_ABYSSAL,
		CAEntities.PUNCTURE_FISH,
		CAEntities.REAPER_FISH,
		CAEntities.UMBRELLA_ABYSSAL,
		CAEntities.PREGNANT_FISH,
		CAEntities.FLEE_FISH,
		CAEntities.CHEST_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ELITE_POOL = {
		CAEntities.OCEANIZED_VINDICATOR,
		CAEntities.OCEANIZED_EVOKER,
		CAEntities.OCEANIZED_RAVAGER,
		CAEntities.OCEANIZED_ENDERMAN,
		CAEntities.COMPASSION_PRAYER
	};

	public static void execute(LevelAccessor world, double xx, double yy, double zz) {
		if ((world.getFluidState(BlockPos.containing(xx, yy, zz)).createLegacyBlock()).getBlock() == Blocks.WATER) {
			int rand = Mth.nextInt(RandomSource.create(), 0, WATER_ELITE_POOL.length - 1);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = ((EntityType<?>) WATER_ELITE_POOL[rand].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
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
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) LAND_ELITE_POOL[rand].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				int rand1 = Mth.nextInt(RandomSource.create(), 0, OCEANIZED_ELITE_POOL.length - 1);
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = ((EntityType<?>) OCEANIZED_ELITE_POOL[rand1].get()).spawn(_level, BlockPos.containing(xx, yy, zz), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CLOUD, xx, yy, zz, 32, 1, 1, 1, 0.1);
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(xx, yy, zz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.phantom.swoop")), SoundSource.NEUTRAL, 1, 1);
		}
	}
}