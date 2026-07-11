package com.apocalypse.caerulaarbor.manager;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.apocalypse.caerulaarbor.init.CAConfigs;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
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
				for (Entity entityiterator : new ArrayList<>(world.players())) {
					if (entityiterator instanceof ServerPlayer player) {
						Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_experience_evolution"));
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
					prefix = "鎼俻";
				} else if (stra == 2) {
					num = "II";
					prefix = "鎼俠";
				} else if (stra == 3) {
					num = "III";
					prefix = "鎼?";
				} else if (stra == 4) {
					num = "IV";
					prefix = "鎼?";
				}
				if (CAConfigs.EVOSOUND.get()) {
					for (Entity entityiterator : new ArrayList<>(world.players())) {
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
			for (Entity entityiterator : new ArrayList<>(world.players())) {
				if (entityiterator instanceof ServerPlayer player) {
					Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "to_terminate_evolution"));
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
}
