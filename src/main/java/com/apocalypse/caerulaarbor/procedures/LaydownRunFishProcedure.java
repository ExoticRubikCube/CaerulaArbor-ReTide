package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class LaydownRunFishProcedure {
	public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Direction direction, Entity entity, ItemStack itemstack) {
		if (direction == null || entity == null)
			return InteractionResult.PASS;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.bucket.empty_fish")), SoundSource.NEUTRAL, 1, 1);
		}
		if (itemstack.getItem() == CAItems.BUCKET_RUNFISH.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.RUN_FISH.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (itemstack.getItem() == CAItems.BUCKET_CHISELER.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.CHISELER_FISH.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (itemstack.getItem() == CAItems.BUCKET_FLOATER.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.FLOATER_PROKARYOTE.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (itemstack.getItem() == CAItems.BUCKET_BONEFISH.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.BONE_FISH.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (itemstack.getItem() == CAItems.BUCKET_COLLECTOR.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.COLLECTOR_PROKARYOTE.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (itemstack.getItem() == CAItems.BUCKET_SLIDER.get()) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CAEntities.SLIDER_FISH.get().spawn(_level, BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ()), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
		itemstack.shrink(1);
		if (entity instanceof Player _player) {
			ItemStack _setstack = new ItemStack(Items.BUCKET).copy();
			_setstack.setCount(1);
			ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
		}
		return InteractionResult.SUCCESS;
	}
}