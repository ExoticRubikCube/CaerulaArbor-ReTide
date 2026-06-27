package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.BlockEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.utils.WorldUtils;

@Mod.EventBusSubscriber
public class SummonOceanWitherEventHandler {
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		LevelAccessor world = event.getLevel();
		double x = event.getPos().getX();
		double y = event.getPos().getY();
		double z = event.getPos().getZ();
		BlockState placedBlockState = event.getState();
		BlockState placedAgainstState = event.getPlacedAgainst();
		Entity entity = event.getEntity();
		if (entity == null)
			return;
		boolean summonedOceanWither = false;
		if (world.getDifficulty() == Difficulty.PEACEFUL) {
			return;
		}
		if (placedBlockState.getBlock() == Blocks.WITHER_SKELETON_SKULL && placedAgainstState.getBlock() == CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get()) {
			if (WorldUtils.checkTShape(world, x, y, z, CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(x + 0.5, y - 2, z + 0.5), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				summonedOceanWither = true;
			} else {
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					double adjacentSkullX = x + direction.getStepX();
					double adjacentSkullZ = z + direction.getStepZ();
					if ((world.getBlockState(BlockPos.containing(adjacentSkullX, y, adjacentSkullZ))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
						if (WorldUtils.checkTShape(world, adjacentSkullX, y, adjacentSkullZ, CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(adjacentSkullX + 0.5, y - 2, adjacentSkullZ + 0.5),
										MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
							summonedOceanWither = true;
							break;
						}
					}
				}
			}
			if (summonedOceanWither && entity instanceof ServerPlayer player) {
				Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "tranquil_heights"));
				AdvancementProgress advancementProgress = player.getAdvancements().getOrStartProgress(advancement);
				if (!advancementProgress.isDone()) {
					for (String criteria : advancementProgress.getRemainingCriteria())
						player.getAdvancements().award(advancement, criteria);
				}
			}
		}
	}
}
