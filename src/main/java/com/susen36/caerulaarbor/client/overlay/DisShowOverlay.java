package com.susen36.caerulaarbor.client.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.util.RelicUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class DisShowOverlay {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (RelicUtils.hasDiso(entity)) {
			if (RelicUtils.hasDiso(entity)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/disoclution_bg.png"), w - 64, h - 128, 0, 0, 64, 128, 64, 128);
			}
			if (RelicUtils.hasDisoAttention(entity)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/disoclution_attention.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (RelicUtils.hasDisoBlood(entity)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/disoclution_blood.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (RelicUtils.hasDisoNeuro(entity)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/disoclution_neuro.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (RelicUtils.hasDisoFlesh(entity)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/disoclution_flesh.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
