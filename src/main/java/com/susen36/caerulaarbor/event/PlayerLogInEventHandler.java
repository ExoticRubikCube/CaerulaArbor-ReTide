package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAGameRules;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class PlayerLogInEventHandler {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			ResourceLocation relicBanNoticeId = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "ban_relic_notice");
			ResourceLocation surgingWavesNoticeId = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "surging_waves_notice");

			if (CAConfigs.RELIC_BAN.get()) {
				Advancement relicBanNoticeAdvancement = serverPlayer.server.getAdvancements().getAdvancement(relicBanNoticeId);
				if (relicBanNoticeAdvancement != null) {
					AdvancementProgress relicBanNoticeProgress = serverPlayer.getAdvancements().getOrStartProgress(relicBanNoticeAdvancement);
					if (!relicBanNoticeProgress.isDone()) {
						for (String remainingCriterion : relicBanNoticeProgress.getRemainingCriteria()) {
							serverPlayer.getAdvancements().award(relicBanNoticeAdvancement, remainingCriterion);
						}
						serverPlayer.displayClientMessage(Component.translatable("gameplay.relic_ban.notice.0"), false);
						serverPlayer.displayClientMessage(Component.translatable("gameplay.relic_ban.notice.1"), false);
						serverPlayer.displayClientMessage(Component.translatable("gameplay.relic_ban.notice.2"), false);
					}
				}
			}

			Advancement surgingWavesNoticeAdvancement = serverPlayer.server.getAdvancements().getAdvancement(surgingWavesNoticeId);
			if (surgingWavesNoticeAdvancement != null) {
				AdvancementProgress surgingWavesNoticeProgress = serverPlayer.getAdvancements().getOrStartProgress(surgingWavesNoticeAdvancement);
				if (!surgingWavesNoticeProgress.isDone()) {
					for (String remainingCriterion : surgingWavesNoticeProgress.getRemainingCriteria()) {
						serverPlayer.getAdvancements().award(surgingWavesNoticeAdvancement, remainingCriterion);
					}

					if (serverPlayer.server.getDefaultGameType() == GameType.SURVIVAL) {
						int surgingWavesLevel = serverPlayer.serverLevel().getGameRules().getInt(CAGameRules.SURGING_WAVES);
						if (surgingWavesLevel >= 12) {
							serverPlayer.displayClientMessage(Component.translatable("gameplay.caerula_arbor.n_warn_12"), false);
						} else if (surgingWavesLevel >= 6) {
							serverPlayer.displayClientMessage(Component.translatable("gameplay.caerula_arbor.n_warn_6"), false);
						}
					}
				}
			}
		}
	}
}
