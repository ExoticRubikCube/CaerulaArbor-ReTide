package com.susen36.caerulaarbor.client.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber({Dist.CLIENT})
public class PalsyOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
            entity.level();
        }
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
        boolean result = false;
        if (entity != null) {
            result = ((Entity) entity instanceof LivingEntity livingEntity0 && livingEntity0.getAttributes().hasAttribute(CAAttributes.NUMB.get()) ? livingEntity0.getAttribute(CAAttributes.NUMB.get()).getBaseValue() : 0) > 0;
        }
        if (result) {
			event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/overlay/palsy.png"), w / 2 + 5, h / 2 + -8, 0, 0, 16, 16, 16, 16);

			event.getGuiGraphics().drawString(Minecraft.getInstance().font,

					EntityUtils.getPalsy(entity), w / 2 + 17, h / 2 + -3, -13421773, false);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,

					EntityUtils.getPalsy(entity), w / 2 + 16, h / 2 + -3, -1, false);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
