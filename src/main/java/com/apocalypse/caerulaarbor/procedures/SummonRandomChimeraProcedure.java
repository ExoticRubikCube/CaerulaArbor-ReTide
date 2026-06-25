package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.entity.IzumikEntity;
import com.apocalypse.caerulaarbor.entity.MegaChestEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public class SummonRandomChimeraProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double r = Math.random();
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.lingering_potion.throw")), SoundSource.HOSTILE, 3, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.lingering_potion.throw")), SoundSource.HOSTILE, 3, 1, false);
			}
		}
		Entity entityToSpawn = null;
		BlockPos pos = BlockPos.containing(x, y+3, z);
		if (world instanceof ServerLevel _level){
			if (r < 0.01) {
				entityToSpawn = CaerulaArborModEntities.IZUMIK.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn instanceof IzumikEntity _datEntSetI)
					_datEntSetI.getEntityData().set(IzumikEntity.DATA_growth_p, 20);
			} else if (r < 0.02) {
				entityToSpawn = CaerulaArborModEntities.TIDE_CHIMERA.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
			} else if (r < 0.21) {
				entityToSpawn = CaerulaArborModEntities.TIDE_DEATHREPELLER.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
			} else if (r < 0.4) {
				entityToSpawn = CaerulaArborModEntities.LINGERING_PATHSHAPER.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
			} else if (r < 0.6){
				entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_3.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);

			}
 else if (r < 0.8) {
				entityToSpawn = CaerulaArborModEntities.FIRST_TO_TALK.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
			} else {
				entityToSpawn = CaerulaArborModEntities.MEGA_CHEST.get().spawn(_level, pos, MobSpawnType.MOB_SUMMONED);
				if (entity instanceof MegaChestEntity _datEntSetL)
					_datEntSetL.getEntityData().set(MegaChestEntity.DATA_released, true);
			}
		}
		if (entityToSpawn != null) {
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				entityToSpawn.push((entity.getLookAngle().x), 0.25, (entity.getLookAngle().z));
			}
	}
}

// TODO: 调用次数 = 3，副作用密集（生成实体、播放声音、修改实体数据），保持原样不重构
