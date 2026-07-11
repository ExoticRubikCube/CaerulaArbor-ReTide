package com.apocalypse.caerulaarbor.client.overlay;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.apocalypse.caerulaarbor.init.CAConfigs;
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
	public static final ResourceLocation SANITY = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/ep/sanity.png");
	public static final ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/ep/sanity_player_bar.png");
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
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
