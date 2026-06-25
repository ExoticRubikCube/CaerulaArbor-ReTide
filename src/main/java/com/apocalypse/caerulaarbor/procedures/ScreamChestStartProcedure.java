package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.entity.ScreamChestFishEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public class ScreamChestStartProcedure {
	public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return InteractionResult.PASS;
		if (entity.isShiftKeyDown()) {
			if (entity instanceof ScreamChestFishEntity) {
				((ScreamChestFishEntity) entity).setAnimation("animation.scream_chest_fish.open");
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.chest.open")), SoundSource.HOSTILE, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.chest.open")), SoundSource.HOSTILE, 1, 1, false);
				}
			}
			entity.setShiftKeyDown(false);
			if (entity instanceof ScreamChestFishEntity _datEntSetL)
				_datEntSetL.getEntityData().set(ScreamChestFishEntity.DATA_release, true);
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
			if (entity instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
				_entity.setTarget(_ent);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}

// TODO: 调用次数 = 4，副作用密集（声音、动画、效果），保持原样不重构
