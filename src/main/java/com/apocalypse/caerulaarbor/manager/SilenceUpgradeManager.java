package com.apocalypse.caerulaarbor.manager;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.StrategyUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;

public class SilenceUpgradeManager {
	public static void execute(LevelAccessor world, double point) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_silence;
		if (StrategyUtils.canEnableSilence(world)) {
			MapVariablesHandler.addEvoPoint(world, StrategyType.SILENCE, point);
			if (stra > 0) {
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "she_coming"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
			}
			if (stra < 4) {
				if (MapVariables.get(world).evo_point_silence >= Math.pow(stra + 1, 3) * CaerulaConfigsConfiguration.COEFFICIENT.get() * 8) {
					MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, stra + 1);
					stra = MapVariables.get(world).strategy_silence;
					MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
					if (stra == 1) {
						num = "I";
						prefix = "§p";
					} else if (stra == 2) {
						num = "II";
						prefix = "§p";
					} else if (stra == 3) {
						num = "III";
						prefix = "§c";
					} else if (stra == 4) {
						num = "IV";
						prefix = "§4";
					}
					if (CaerulaConfigsConfiguration.EVOSOUND.get()) {
						for (Entity entityiterator : new ArrayList<>(world.players())) {
							if (stra == 1) {
								if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE1.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_6").getString())), true);
							} else if (stra == 2) {
								if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE2.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_7").getString())), true);
							} else if (stra == 3) {
								if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE3.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_8").getString())), true);
							} else if (stra == 4) {
								if (world instanceof Level _level) {
										_level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE4.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.language_key.description_9").getString())), true);
							}
						}
					}
					if (!world.isClientSide() && world.getServer() != null)
						world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.language_key.description_4").getString() + num)), false);
				}
			} else {
				MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "hymn_of_land"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
			}
		} else {
			MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
		}
	}
}
