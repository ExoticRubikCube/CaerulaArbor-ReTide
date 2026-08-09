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

public class GrowUpgradeManager {
	public static void applyGrowthUpgrade(LevelAccessor world) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_grow;
		if (stra < 4) {
			if (MapVariables.get(world).evo_point_grow >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get()) {
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
				MapVariablesHandler.setStrategyLevel(world, StrategyType.GROW, stra + 1);
				stra = MapVariables.get(world).strategy_grow;
				MapVariablesHandler.setEvoPoint(world, StrategyType.GROW, 0);
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
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.GROW2.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						} else if (stra > 0) {
							if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.GROW1.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						}
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.sample_grow.description_5").getString() + num)), false);
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
			MapVariablesHandler.setEvoPoint(world, StrategyType.GROW, 1);
		}
	}

	public static double getStraGrow(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow;
	}

	public static int getGrowAttackPct(double level) {
		return 25 * (int) level;
	}

	public static double getGrowAttackMultiplier(double level) {
		return 1.0D + 0.25D * level;
	}

	public static int getGrowMagicPct(double level) {
		if (level >= 3) {
			return 20 * ((int) level - 2);
		}
		return 0;
	}

	public static double getGrowMagicMultiplier(double level) {
		if (level >= 3) {
			return 0.2D * (level - 2.0D);
		}
		return 0.0D;
	}

	public static int getSilenceGrowBonusPct(double silenceLevel) {
		return 25 * (int) silenceLevel;
	}

	public static boolean isSilenceGrowFullHpTrigger(double silenceLevel) {
		return silenceLevel >= 3;
	}

	public static String getDescrGrow(LevelAccessor world) {
		double level = MapVariables.get(world).strategy_grow;
		if (level <= 0) {
			return Component.translatable("item.caerula_arbor.sample_grow.description_0").getString();
		}
		int atk = getGrowAttackPct(level);
		int magic = getGrowMagicPct(level);
		if (magic > 0) {
			return Component.translatable("item.caerula_arbor.sample_grow.description_with_magic", atk, magic).getString();
		}
		return Component.translatable("item.caerula_arbor.sample_grow.description_basic", atk).getString();
	}

	public static String getDescrSilenceGrow(LevelAccessor world) {
		double silenceLevel = MapVariables.get(world).strategy_silence;
		int bonusPct = getSilenceGrowBonusPct(silenceLevel);
		if (isSilenceGrowFullHpTrigger(silenceLevel)) {
			return Component.translatable("item.caerula_arbor.sample_grow.silence_full_hp", bonusPct).getString();
		}
		return Component.translatable("item.caerula_arbor.sample_grow.silence_below_half", bonusPct).getString();
	}

	public static String getCmdFeedback(long lvl) {
		return Component.translatable("command.evolution.grow").getString().replace("<num>", "" + lvl);
	}

	public static String getDisplayName() {
		return Component.translatable("gui.caerula_arbor.evo_tree.label_sreategy_grow").getString();
	}
}