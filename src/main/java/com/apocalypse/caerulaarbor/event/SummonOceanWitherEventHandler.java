package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			if (placedBlockState.getBlock() == Blocks.WITHER_SKELETON_SKULL && placedAgainstState.getBlock() == CABlocks.NETHERSEA_SOUL_SAND.get()) {
				if (checkTShape(world, x, y, z, CABlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CAEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(x + 0.5, y - 2, z + 0.5), MobSpawnType.MOB_SUMMONED);
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
							if (checkTShape(world, adjacentSkullX, y, adjacentSkullZ, CABlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
								if (world instanceof ServerLevel _level) {
									Entity entityToSpawn = CAEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(adjacentSkullX + 0.5, y - 2, adjacentSkullZ + 0.5),
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

	private static boolean checkTShape(LevelAccessor world, double x, double y, double z, BlockState target) {
		double direction = 0;
		boolean verticalMatched = false;
		boolean horizontalMatched = false;
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == target.getBlock()) {
			verticalMatched = true;
		}
		if ((world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == target.getBlock()
				&& (world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == Blocks.WITHER_SKELETON_SKULL && (world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
			horizontalMatched = true;
			direction = 0;
		} else if ((world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == target.getBlock() && (world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == target.getBlock()
				&& (world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == Blocks.WITHER_SKELETON_SKULL && (world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
			horizontalMatched = true;
			direction = 1;
		}
		if (horizontalMatched && verticalMatched) {
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
			world.destroyBlock(BlockPos.containing(x, y - 2, z), false);
			if (direction == 0) {
				world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
			} else {
				world.destroyBlock(BlockPos.containing(x, y, z - 1), false);
				world.destroyBlock(BlockPos.containing(x, y, z + 1), false);
				world.destroyBlock(BlockPos.containing(x, y - 1, z - 1), false);
				world.destroyBlock(BlockPos.containing(x, y - 1, z + 1), false);
			}
			return true;
		}
		return false;
	}
}
