package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CAConfigs;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class PlayerLogInEventHandler {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			ResourceLocation relicBanNoticeId = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "ban_relic_notice");
			if (CAConfigs.RELIC_BAN.get()) {
				AdvancementHolder relicBanNoticeAdvancement = serverPlayer.server.getAdvancements().get(relicBanNoticeId);
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
	}
}