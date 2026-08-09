package com.susen36.caerulaarbor.manager.upgrade;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;

public class MigrationUpgradeManager {
	public static void applyMigrationUpgrade(LevelAccessor world) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_migration;
		if (stra < 4) {
			if (MapVariables.get(world).evo_point_migration >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get()) {
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_experience_evolution"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
				MapVariablesHandler.setStrategyLevel(world, StrategyType.MIGRATION, stra + 1);
				stra = MapVariables.get(world).strategy_migration;
				MapVariablesHandler.setEvoPoint(world, StrategyType.MIGRATION, 0);
				if (stra == 1) {
					num = "I";
					prefix = "§p";
				} else if (stra == 2) {
					num = "II";
					prefix = "§b";
				} else if (stra == 3) {
					num = "III";
					prefix = "§9";
				} else if (stra == 4) {
					num = "IV";
					prefix = "§1";
				}
				if (CAConfigs.EVOSOUND.get()) {
					for (Player entityiterator : new ArrayList<>(world.players())) {
						if (stra >= 3) {
							if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.MIGRATION2.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						} else if (stra > 0) {
							if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.MIGRATION1.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						}
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.sample_migration.description_5").getString() + num)), false);
			}
		} else {
			for (Player entityiterator : new ArrayList<>(world.players())) {
				if (entityiterator instanceof ServerPlayer player) {
					AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_terminate_evolution"));
					if (adv == null) continue;
					AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
					if (!ap.isDone()) {
						for (String criteria : ap.getRemainingCriteria())
							player.getAdvancements().award(adv, criteria);
					}
				}
			}
			MapVariablesHandler.setEvoPoint(world, StrategyType.MIGRATION, 0);
		}
	}

	public static double getStraMigration(LevelAccessor world) {
		return MapVariables.get(world).strategy_migration;
	}

	public static int[] getMigrationRange(double migrationLevel) {
		if (migrationLevel >= 4) {
			return new int[]{128, 64};
		} else if (migrationLevel >= 3) {
			return new int[]{96, 48};
		} else if (migrationLevel >= 2) {
			return new int[]{64, 32};
		} else {
			return new int[]{32, 32};
		}
	}

	public static int getSilenceMigrationMovePct(double silenceLevel) {
		return 5 * (int) silenceLevel;
	}

	public static int getSilenceMigrationCrowdBonusPct(double silenceLevel) {
		if (silenceLevel >= 3) {
			return 5 * ((int) silenceLevel - 2);
		}
		return 0;
	}

	public static int getSilenceMigrationMaxStacks(double silenceLevel) {
		if (silenceLevel >= 3) {
			return 5 * ((int) silenceLevel - 1);
		}
		return 0;
	}

	public static String getDescrMigra(LevelAccessor world) {
		double level = MapVariables.get(world).strategy_migration;
		if (level <= 0) {
			return Component.translatable("item.caerula_arbor.sample_migration.description_0").getString();
		}
		int[] range = getMigrationRange(level);
		return Component.translatable("item.caerula_arbor.sample_migration.description_call", range[0], range[1], range[0]).getString();
	}

	public static String getDescrSilenceMigra(LevelAccessor world) {
		double silenceLevel = MapVariables.get(world).strategy_silence;
		int movePct = getSilenceMigrationMovePct(silenceLevel);
		int crowdPct = getSilenceMigrationCrowdBonusPct(silenceLevel);
		int maxStacks = getSilenceMigrationMaxStacks(silenceLevel);
		if (crowdPct > 0) {
			return Component.translatable("item.caerula_arbor.sample_migration.silence_with_crowd",
					movePct, crowdPct, maxStacks).getString();
		}
		return Component.translatable("item.caerula_arbor.sample_migration.silence_basic", movePct).getString();
	}

	public static String getCmdFeedback(long lvl) {
		return Component.translatable("command.evolution.migration").getString().replace("<num>", "" + lvl);
	}

	public static String getDisplayName() {
		return Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_migration").getString();
	}
}