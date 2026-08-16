package com.susen36.caerulaarbor.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
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
public class DisoclutionOverlay {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
			int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			double rejectionStage = ModCapabilities.getPlayerVariables(entity).disoclusion;
			if (rejectionStage != 0) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_bg.png"), w - 64, h - 128, 0, 0, 64, 128, 64, 128);
				if (rejectionStage == 1) {
					event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_attention.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
				} else if (rejectionStage == 2) {
					event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_blood.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
				} else if (rejectionStage == 3) {
					event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_neuro.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
				} else if (rejectionStage == 4) {
					event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_flesh.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
				}
			}
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
}
