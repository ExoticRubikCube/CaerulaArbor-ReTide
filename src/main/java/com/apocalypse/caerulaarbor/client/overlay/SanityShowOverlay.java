package com.apocalypse.caerulaarbor.client.overlay;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class SanityShowOverlay {
	public static final ResourceLocation SANITY = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/sanity.png");
	public static final ResourceLocation BAR = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/sanity_player_bar.png");
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			double sanity = ModCapabilities.getSanityInjury(entity).getValue();
			if (sanity >= 1000) {
				return;
			}
			int dx = getOverlayOffsetX();
			int dy = getOverlayOffsetY();
			if (CaerulaConfigsConfiguration.SANITY_BAR_STYLE.get()){
				event.getGuiGraphics().blit(BAR, w / 2 + 93 + dx, h - 12 + dy, 
				0, 4, 62, 8, 62, 12);
				event.getGuiGraphics().blit(BAR, w / 2 + 93 + dx + 10, h - 12 + dy + 3,
				0, 0, (int) (50 * sanity / 1000), 4, 62, 12);

			} else {
				event.getGuiGraphics().blit(SANITY, w / 2 + 92 + dx, h - 19 + dy, 
				Math.max(0, Math.min((int) Math.ceil(sanity / 50) * 16, 304)), 0, 16, 16, 320, 16);
			}
		}
	}

	private static int getOverlayOffsetX() {
		return Math.toIntExact(Math.round(CaerulaConfigsConfiguration.X_OFFSET.get()));
	}

	private static int getOverlayOffsetY() {
		return Math.toIntExact(Math.round(CaerulaConfigsConfiguration.Y_OFFSET.get()));
	}
}
