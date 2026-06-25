package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class TrailReplaceProcedure {
	public static void execute(LevelAccessor world, BlockState toPlace, boolean water, double xx, double yy, double zz) {
		if (!world.isClientSide()) {
			if ((world.getBlockState(BlockPos.containing(xx, yy, zz))).getDestroySpeed(world, BlockPos.containing(0, 0, 0)) >= 0) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "start_of_calamity"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
				{
					BlockPos _pos = BlockPos.containing(xx, yy, zz);
					Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(xx + 0.5, yy, zz + 0.5), null);
					world.destroyBlock(_pos, false);
				}
				world.setBlock(BlockPos.containing(xx, yy, zz), (toPlace.getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _withbp6 ? toPlace.setValue(_withbp6, water) : toPlace), 3);
				world.levelEvent(2001, BlockPos.containing(xx, yy, zz), Block.getId(CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState()));
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(xx, yy, zz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(xx, yy, zz, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.place")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
			}
		}
	}
}

// TODO: 调用次数 = 16，但副作用密集（修改方块、播放声音、给予玩家成就、掉落方块资源），保持原样不重构
