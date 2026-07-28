package com.susen36.caerulaarbor.client.overlay;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.susen36.caerulaarbor.init.CAConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class SanityShowOverlay {
	public static final ResourceLocation SANITY = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/ep/sanity.png");
	public static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/ep/sanity_player_bar.png");
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			SanityInjuryCapability sanityCapability = ModCapabilities.getSanityInjury(entity);
			double sanity = sanityCapability.getValue();
			double maxSanity = sanityCapability.getMaxValue();
			if (sanity >= maxSanity) {
				return;
			}
			int dx = getOverlayOffsetX();
			int dy = getOverlayOffsetY();
			if (CAConfigs.SANITY_BAR_STYLE.get()){
				event.getGuiGraphics().blit(BAR, w / 2 + 93 + dx, h - 12 + dy, 
				0, 4, 62, 8, 62, 12);
				event.getGuiGraphics().blit(BAR, w / 2 + 93 + dx + 10, h - 12 + dy + 3,
				0, 0, (int) (50 * sanity / maxSanity), 4, 62, 12);

			} else {
				event.getGuiGraphics().blit(SANITY, w / 2 + 92 + dx, h - 19 + dy, 
				Math.max(0, Math.min((int) Math.ceil(sanity / maxSanity * 20.0) * 16, 304)), 0, 16, 16, 320, 16);
			}
		}
	}

	private static int getOverlayOffsetX() {
		return Math.toIntExact(Math.round(CAConfigs.X_OFFSET.get()));
	}

	private static int getOverlayOffsetY() {
		return Math.toIntExact(Math.round(CAConfigs.Y_OFFSET.get()));
	}
}