package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
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
		CaerulaArborModEntities.APOSTLE_PROKARYOTE,
		CaerulaArborModEntities.NUCLEIC_MALEFICENT
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] LAND_ELITE_POOL = {
		CaerulaArborModEntities.BASELAYER_ABYSSAL,
		CaerulaArborModEntities.CRACKER_ABYSSAL,
		CaerulaArborModEntities.CREEPER_FISH,
		CaerulaArborModEntities.GUIDE_ABYSSAL,
		CaerulaArborModEntities.PUNCTURE_FISH,
		CaerulaArborModEntities.REAPER_FISH,
		CaerulaArborModEntities.UMBRELLA_ABYSSAL,
		CaerulaArborModEntities.PREGNANT_FISH,
		CaerulaArborModEntities.FLEE_FISH,
		CaerulaArborModEntities.CHEST_FISH
	};

	@SuppressWarnings("rawtypes")
	private static final RegistryObject[] OCEANIZED_ELITE_POOL = {
		CaerulaArborModEntities.OCEANIZED_VINDICATOR,
		CaerulaArborModEntities.OCEANIZED_EVOKER,
		CaerulaArborModEntities.OCEANIZED_RAVAGER,
		CaerulaArborModEntities.OCEANIZED_ENDERMAN,
		CaerulaArborModEntities.COMPASSION_PRAYER
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