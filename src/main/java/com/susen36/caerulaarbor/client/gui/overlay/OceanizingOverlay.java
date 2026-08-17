package com.susen36.caerulaarbor.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CAMobEffects;
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
public class OceanizingOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ResourceLocation texture = null;
			float opacity = 0.0F;
			if (player.hasEffect(CAMobEffects.INFESTED)) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/transforming1.png");
				int amplifier = player.getEffect(CAMobEffects.INFESTED).getAmplifier();
				if (amplifier == 0) {
					opacity = 0.35F;
				} else if (amplifier == 1) {
					opacity = 0.65F;
				} else if (amplifier > 1) {
					opacity = 0.95F;
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
				RenderSystem.setShaderColor(1, 1, 1, opacity);
				event.getGuiGraphics().blit(texture, 0, 0, 0, 0, width, height, width, height);
				RenderSystem.depthMask(true);
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}
		}
	}
}
