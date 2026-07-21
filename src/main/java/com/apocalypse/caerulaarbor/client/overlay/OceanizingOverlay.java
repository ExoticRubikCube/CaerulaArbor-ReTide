package com.apocalypse.caerulaarbor.client.overlay;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class OceanizingOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		Player player = Minecraft.getInstance().player;
		ResourceLocation texture = null;
		if (player.hasEffect(CAMobEffects.INFESTED.get())) {
			int amplifier = player.getEffect(CAMobEffects.INFESTED.get()).getAmplifier();
			if (amplifier == 0) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/transforming0.png");
			} else if (amplifier == 1) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/transforming1.png");
			} else if (amplifier > 1) {
				texture = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/transforming2.png");
			}
		}
		if (texture != null) {
			int width = event.getWindow().getGuiScaledWidth();
			int height = event.getWindow().getGuiScaledHeight();
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
