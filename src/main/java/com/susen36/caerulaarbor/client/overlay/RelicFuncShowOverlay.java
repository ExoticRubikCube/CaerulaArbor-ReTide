package com.susen36.caerulaarbor.client.overlay;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class RelicFuncShowOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player entity = Minecraft.getInstance().player;
        boolean result2 = false;
        if (entity != null) {
            result2 = !(ModCapabilities.getPlayerVariables(entity)).kingShowPtc;
        }
        if (result2) {

            double result = 0;
            result = (ModCapabilities.getPlayerVariables(entity)).player_king_suit;
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/icon_king.png"), 6, 8, Mth.clamp((int) result * 16, 0, 32), 0, 16, 16, 48, 16);

            double result3 = 0;
            result3 = (ModCapabilities.getPlayerVariables(entity)).player_demon_suit;
            event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/icon_artifi.png"), 22, 8, Mth.clamp((int) result3 * 16, 0, 32), 0, 16, 16, 48, 16);

            boolean result1 = entity.hasEffect(CAMobEffects.TIDE_OF_CHITIN);
            if (result1) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/overlay/icon_chitin.png"), 38, 8, 0, 0, 16, 16, 16, 16);
			}
		}
	}
}