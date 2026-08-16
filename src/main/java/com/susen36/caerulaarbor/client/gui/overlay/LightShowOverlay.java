package com.susen36.caerulaarbor.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class LightShowOverlay {
	public static final ResourceLocation EXTINGUISH = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_extinguish.png");
	public static final ResourceLocation DIM = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_dim.png");
	public static final ResourceLocation WAVING = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_waving.png");
	public static final ResourceLocation BRIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light.png");

	public static final ResourceLocation NEAT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/lights_neat.png");


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		if (entity == null || entity.isSpectator())
			return;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
        boolean result1;
        result1 = entity.isAlive() && ModCapabilities.getPlayerVariables(entity).show_stats;
        if (result1) {
			boolean isNeat = CAConfigs.LIGHTS_NEAT_STYLE.get();
			int lightDx = CAConfigs.X_OFFSET_LIGHT.get().intValue();
			int lightDy = CAConfigs.Y_OFFSET_LIGHT.get().intValue();
			if (PlayerStateUtils.isLightCeased(entity)) {
				if (isNeat){
					event.getGuiGraphics().blit(NEAT,
					w / 2 - 9 + lightDx, 2 + lightDy, 18, 0, 6, 13, 24, 13);
				} else {
					event.getGuiGraphics().blit(EXTINGUISH,
					w / 2 - 32 + lightDx, lightDy, 0, 0, 64, 32, 64, 32);
				}
			}
			if (PlayerStateUtils.isLightDim(entity)) {
				if (isNeat){
					event.getGuiGraphics().blit(NEAT,
					w / 2 - 9 + lightDx, 2 + lightDy, 12, 0, 6, 13, 24, 13);
				} else {
					event.getGuiGraphics().blit(DIM, 
					w / 2 - 32 + lightDx, lightDy, 0, 0, 64, 32, 64, 32);
				}
			}
			if (PlayerStateUtils.isLightWaving(entity)) {
				if (isNeat) {
					event.getGuiGraphics().blit(NEAT,
					w / 2 - 9 + lightDx, 2 + lightDy, 6, 0, 6, 13, 24, 13);
				} else {
					event.getGuiGraphics().blit(WAVING, 
					w / 2 + -32 + lightDx, lightDy, 0, 0, 64, 32, 64, 32);
				}
			}
			if (PlayerStateUtils.isLightBright(entity)) {
				if (isNeat) {
					event.getGuiGraphics().blit(NEAT,
					w / 2 - 9 + lightDx, 2 + lightDy, 0, 0, 6, 13, 24, 13);
				} else {
					event.getGuiGraphics().blit(BRIGHT,
					w / 2 + -32 + lightDx, lightDy, 0, 0, 64, 32, 64, 32);
				}
			}

			String light = EntityUtils.getLight(entity);
			if (isNeat) {
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						light, w / 2 - 1 + lightDx, 7 + lightDy, -13421773, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						light, w / 2 - 2 + lightDx, 7 + lightDy, -1, false);
			} else {
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						light, w / 2 - 5 + lightDx, 26 + lightDy, -13421773, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						light, w / 2 - 6 + lightDx, 26 + lightDy, -1, false);
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}