package com.susen36.caerulaarbor.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.babel.collectible.Collectibles;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CACollectible;
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
public class DisShowOverlay {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO)) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/disoclution_bg.png"), w - 64, h - 128, 0, 0, 64, 128, 64, 128);
			}
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_ATTENTION)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/disoclution_attention.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_BLOOD)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/disoclution_blood.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_NEURO)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/disoclution_neuro.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_FLESH)) {
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/disoclution_flesh.png"), w - 69, h - 72, 0, 0, 64, 64, 64, 64);
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}