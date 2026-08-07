package com.susen36.caerulaarbor.manager.upgrade;

import com.susen36.caerulaarbor.CaerulaArborMod;
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

public class BreedUpgradeManager {
	public static void applyBreedUpgrade(LevelAccessor world) {
		double stra;
		String num = "";
		String prefix = "";
		stra = MapVariables.get(world).strategy_breed;
		if (stra < 4) {
			if (MapVariables.get(world).evo_point_breed >= Math.pow(stra + 1, 3) * CAConfigs.COEFFICIENT.get()) {
				for (Player entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_experience_evolution"));
						if (adv == null) continue;
						AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
						if (!ap.isDone()) {
							for (String criteria : ap.getRemainingCriteria())
								player.getAdvancements().award(adv, criteria);
						}
					}
				}
				MapVariablesHandler.setStrategyLevel(world, StrategyType.BREED, stra + 1);
				stra = MapVariables.get(world).strategy_breed;
				MapVariablesHandler.setEvoPoint(world, StrategyType.BREED, 0);
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
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.BREED2.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						} else if (stra > 0) {
							if (world instanceof Level level) {
									level.playSound(null, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), CASounds.BREED1.get(),
											SoundSource.NEUTRAL, 4, 1);
							}
						}
					}
				}
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((prefix + Component.translatable("item.caerula_arbor.sample_breed.description_5").getString() + num)), false);
			}
		} else {
			for (Player entityiterator : new ArrayList<>(world.players())) {
				if (entityiterator instanceof ServerPlayer player) {
					AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_terminate_evolution"));
					if (adv == null) continue;
					AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
					if (!ap.isDone()) {
						for (String criteria : ap.getRemainingCriteria())
							player.getAdvancements().award(adv, criteria);
					}
				}
			}
			MapVariablesHandler.setEvoPoint(world, StrategyType.BREED, 1);
		}
	}
}