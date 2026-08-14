package com.susen36.caerulaarbor.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
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
public class LessArmorOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player player = Minecraft.getInstance().player;
		ResourceLocation texture = null;
		if (player.hasEffect(BabelMobEffects.LESS_ARMOR)) {
			int amplifier = player.getEffect(BabelMobEffects.LESS_ARMOR).getAmplifier();
			if (amplifier <= 4) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/low_armor_ui.png");
			} else if (amplifier <= 9) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/less_armor_ui.png");
			} else {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/least_armor_ui.png");
			}
		}
		if (texture != null) {
			int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
			int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			event.getGuiGraphics().blit(texture, 0, 0, 0, 0, width, height, width, height);
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
}