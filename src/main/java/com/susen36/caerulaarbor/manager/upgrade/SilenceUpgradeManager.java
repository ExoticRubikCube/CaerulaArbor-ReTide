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

public class SilenceUpgradeManager {
	public static void applySilenceUpgrade(LevelAccessor world, double point) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_silence;
		if (canEnableSilence(world)) {
			MapVariablesHandler.addEvoPoint(world, StrategyType.SILENCE, point);
			if (stra > 0) {
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "she_coming"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
			}
			if (stra < 4) {
				if (MapVariables.get(world).evo_point_silence >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get() * 8) {
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
					if (CAConfigs.EVOSOUND.get()) {
						for (Player entityiterator : new ArrayList<>(world.players())) {
							if (stra == 1) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE1.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(1)), true);
							} else if (stra == 2) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE2.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(2)), true);
							} else if (stra == 3) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE3.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(3)), true);
							} else if (stra == 4) {
								if (world instanceof Level level) {
										level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SILENCE4.get(),
												SoundSource.NEUTRAL, 6, 1);
								}
								if (entityiterator instanceof Player player && !player.level().isClientSide())
									player.displayClientMessage(Component.literal(getSilenceUnlockPlayerMsg(4)), true);
							}
						}
					}
					if (!world.isClientSide() && world.getServer() != null)
						world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.language_key.description_4").getString() + num)), false);
				}
			} else {
				MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "hymn_of_land"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
			}
		} else {
			MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, 0);
		}
	}

	public static boolean isSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence > 0;
	}

	public static boolean canEnableSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow >= 4 && MapVariables.get(world).strategy_subsisting >= 4 && MapVariables.get(world).strategy_breed >= 4
				&& MapVariables.get(world).strategy_migration >= 4 && MapVariables.get(world).silence_enabled;
	}

	public static double getStraSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence;
	}

	public static String getSilenceMigration(LevelAccessor world) {
		return MigrationUpgradeManager.getDescrSilenceMigra(world);
	}

	public static String getSilenceSubsis(LevelAccessor world) {
		return SubsistingUpgradeManager.getDescrSilenceSubsis(world);
	}

	public static String getSilenceBreed(LevelAccessor world) {
		return BreedUpgradeManager.getDescrSilenceBreed(world);
	}

	public static String getSilenceGrow(LevelAccessor world) {
		return GrowUpgradeManager.getDescrSilenceGrow(world);
	}

	public static String getCmdFeedback(long lvl) {
		return Component.translatable("command.evolution.silence").getString().replace("<num>", "" + lvl);
	}

	public static String getSilenceUnlockPlayerMsg(long lvl) {
		if (lvl == 1) {
			return Component.translatable("item.caerula_arbor.language_key.description_6").getString();
		} else if (lvl == 2) {
			return Component.translatable("item.caerula_arbor.language_key.description_7").getString();
		} else if (lvl == 3) {
			return Component.translatable("item.caerula_arbor.language_key.description_8").getString();
		} else {
			return Component.translatable("item.caerula_arbor.language_key.description_9").getString();
		}
	}

	public static String getSilenceLockedMsg() {
		return Component.translatable("item.caerula_arbor.language_key.description_5").getString();
	}
}