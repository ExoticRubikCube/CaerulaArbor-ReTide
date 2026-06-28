package com.apocalypse.caerulaarbor.client.screens;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.PlayerStateUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class LightShowOverlay {
	public static final ResourceLocation EXTINGUISH = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/light_extinguish.png");
	public static final ResourceLocation DIM = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/light_dim.png");
	public static final ResourceLocation WAVING = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/light_waving.png");
	public static final ResourceLocation BRIGHT = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/light.png");

	public static final ResourceLocation LIFE_POINT = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/target_health.png");
	public static final ResourceLocation SHIELD_POINT = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/target_shield.png");

	public static final ResourceLocation NEAT = new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/lights_neat.png");


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity.isSpectator()) return; 
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
        boolean result1 = false;
        if (entity != null) {
            result1 = ((Entity) entity).isAlive() && (((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).show_stats;
        }
        if (result1) {
			boolean isNeat = CaerulaConfigsConfiguration.LIGHTS_NEAT_STYLE.get();
			int lightDx = CaerulaConfigsConfiguration.X_OFFSET_LIGHT.get().intValue();
			int lightDy = CaerulaConfigsConfiguration.Y_OFFSET_LIGHT.get().intValue();
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

			int lifeDx = CaerulaConfigsConfiguration.X_OFFSET_LIFE.get().intValue();
			int lifeDy = CaerulaConfigsConfiguration.Y_OFFSET_LIFE.get().intValue() - 16;

			event.getGuiGraphics().blit(LIFE_POINT, 
				6 + lifeDx, h - 24 + lifeDy, 0, 0, 24, 16, 24, 16);

            boolean result = false;
            if (entity != null) {
                result = (((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_shield > 0;
            }
            if (result) {
				event.getGuiGraphics().blit(SHIELD_POINT, 
					6 + lifeDx, h - 40 + lifeDy, 0, 0, 24, 16, 24, 16);
				String shield = EntityUtils.getShield(entity);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						shield, 21 + lifeDx, h - 36 + lifeDy, -16777216, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font,
						shield, 20 + lifeDx, h - 36 + lifeDy, -1, false);
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

			String life_max = EntityUtils.getLiveMaxShown(entity), life = EntityUtils.getLives(entity);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,
					life_max, 37 + lifeDx, h - 21 + lifeDy, -16764109, false);
				
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,
					life_max, 36 + lifeDx, h - 21 + lifeDy, -10040065, false);
					
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,
					life, 21 + lifeDx, h - 21 + lifeDy, -16764109, false);
				
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,
					life, 20 + lifeDx, h - 21 + lifeDy, -1, false);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
