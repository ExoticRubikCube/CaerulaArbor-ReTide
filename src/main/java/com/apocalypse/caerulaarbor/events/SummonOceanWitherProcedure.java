package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
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

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class SummonOceanWitherProcedure {
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getState(), event.getPlacedAgainst(), event.getEntity());
	}

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, BlockState placedagainst, Entity entity) {
		if (entity == null)
			return;
		double tx = 0;
		double tz = 0;
		double direc = 0;
		if (world.getDifficulty() == Difficulty.PEACEFUL) {
			return;
		}
		if (blockstate.getBlock() == Blocks.WITHER_SKELETON_SKULL && placedagainst.getBlock() == CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get()) {
			if (WorldUtils.checkTShape(world, x, y, z, CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(x + 0.5, y - 2, z + 0.5), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
			} else {
				for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
					tx = x + directioniterator.getStepX();
					tz = z + directioniterator.getStepZ();
					if ((world.getBlockState(BlockPos.containing(tx, y, tz))).getBlock() == Blocks.WITHER_SKELETON_SKULL) {
						if (WorldUtils.checkTShape(world, tx, y, tz, CaerulaArborModBlocks.NETHERSEA_SOUL_SAND.get().defaultBlockState())) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITHER.get().spawn(_level, BlockPos.containing(tx + 0.5, y - 2, tz + 0.5), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
							if (entity instanceof ServerPlayer _player) {
								Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "tranquil_heights"));
								AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
								if (!_ap.isDone()) {
									for (String criteria : _ap.getRemainingCriteria())
										_player.getAdvancements().award(_adv, criteria);
								}
							}
							break;
						}
					}
				}
			}
		}
	}
}
