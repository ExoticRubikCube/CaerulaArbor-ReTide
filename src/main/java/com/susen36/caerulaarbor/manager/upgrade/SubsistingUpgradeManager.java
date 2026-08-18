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

public class SubsistingUpgradeManager {
	public static void applySubsistingUpgrade(LevelAccessor world) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_subsisting;
		if (stra < 4) {
			if (MapVariables.get(world).evo_point_subsisting >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get()) {
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
				MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBSISTING, stra + 1);
				stra = MapVariables.get(world).strategy_subsisting;
				MapVariablesHandler.setEvoPoint(world, StrategyType.SUBSISTING, 0);
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
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SUBSISTING2.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						} else if (stra > 0) {
							if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.SUBSISTING1.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						}
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.sample_subsisting.description_5").getString().replace("{num}", num))), false);
			}
		} else {
			for (Player entityiterator : new ArrayList<>(world.players())) {
				if (entityiterator instanceof ServerPlayer player) {
					AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_terminate_evolution"));
                    AdvancementProgress ap;
                    if (adv != null) {
						ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
			}
			MapVariablesHandler.setEvoPoint(world, StrategyType.SUBSISTING, 1);
		}
	}

	public static double getStraSubsis(LevelAccessor world) {
		return MapVariables.get(world).strategy_subsisting;
	}

	public static double getSubsistHealthMultiplier(double level) {
		return 1.0D + 0.3D * level;
	}

	public static int getSubsistHealthPct(double level) {
		return 30 * (int) level;
	}

	public static int getSubsistArmorBonus(double level) {
		return 2 * (int) level;
	}

	public static int getSubsistDefenseBonus(double level) {
		return 1 * (int) level;
	}

	public static int getSubsistResistLevel(double level) {
		if (level >= 3) {
			return (int) level - 3;
		}
		return -1;
	}

	public static int getSilenceHealLevel(double silenceLevel) {
		return (int) silenceLevel;
	}

	public static int getSilenceThornsPct(double silenceLevel) {
		if (silenceLevel >= 3) {
			return 15 * ((int) silenceLevel - 2);
		}
		return 0;
	}

	public static String getDescrSubsis(LevelAccessor world) {
		double level = MapVariables.get(world).strategy_subsisting;
		if (level <= 0) {
			return Component.translatable("item.caerula_arbor.sample_subsisting.description_0").getString();
		}
		int hp = getSubsistHealthPct(level);
		int armor = getSubsistArmorBonus(level);
		int def = getSubsistDefenseBonus(level);
		int resist = getSubsistResistLevel(level);
		if (resist >= 0) {
			return Component.translatable("item.caerula_arbor.sample_subsisting.description_with_resist",
					hp, armor, def, resist + 1).getString();
		}
		return Component.translatable("item.caerula_arbor.sample_subsisting.description_basic",
				hp, armor, def).getString();
	}

	public static String getDescrSilenceSubsis(LevelAccessor world) {
		double silenceLevel = MapVariables.get(world).strategy_silence;
		int healLvl = getSilenceHealLevel(silenceLevel);
		int thornsPct = getSilenceThornsPct(silenceLevel);
		if (thornsPct > 0) {
			return Component.translatable("item.caerula_arbor.sample_subsisting.silence_with_thorns",
					toRoman(healLvl), thornsPct).getString();
		}
		return Component.translatable("item.caerula_arbor.sample_subsisting.silence_basic",
				toRoman(healLvl)).getString();
	}

	private static String toRoman(int lvl) {
		if (lvl == 1) return "I";
		if (lvl == 2) return "II";
		if (lvl == 3) return "III";
		return "IV";
	}

	public static String getCmdFeedback(long lvl) {
		return Component.translatable("command.evolution.subsisting").getString().replace("<num>", "" + lvl);
	}

	public static String getDisplayName() {
		return Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_subsisting").getString();
	}
}