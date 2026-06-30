package com.apocalypse.caerulaarbor.client.overlay;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class AttrShowOverlay {

	public static ResourceLocation MISS = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/miss.png");
	public static ResourceLocation BARRIER = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/living_barrier.png");
	public static ResourceLocation RESIS = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/magic_resistance.png");
	public static ResourceLocation DEFENSE = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/defense.png");
	public static ResourceLocation RESIS_BAR = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/magic_resistance_bar.png");
	public static ResourceLocation BARRIER_BAR = new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/living_barrier_bar.png");
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		if (entity.isSpectator()) return;
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		Font font = Minecraft.getInstance().font;

		int dx = CaerulaConfigsConfiguration.X_OFFSET_ATTR.get().intValue();
		int dy = CaerulaConfigsConfiguration.Y_OFFSET_ATTR.get().intValue();
		if (entity.isAlive()) {
            String defense = "";
            {
                double d;
                d = (Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()) ? _livingEntity0.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).getValue() : 0;
                if (d > 0) {
                    defense = new java.text.DecimalFormat("##.#").format(d);
                }
            }
            String resis = "";
            if (entity != null) {
                double d1;
                d1 = (Entity) entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()) ? _livingEntity1.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getValue() : 0;
                if (d1 > 0) {
                    resis = new java.text.DecimalFormat("##.#").format(d1);
                }
            }
            String miss = "";
            if (entity != null) {
                double d;
                d = (Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.MISSRATE.get()) ? _livingEntity0.getAttribute(CAAttributes.MISSRATE.get()).getValue() : 0;
                if (d > 0) {
                    miss = new java.text.DecimalFormat("##.#").format(d);
                }
            }
            String barrier = "";
            if (entity != null) {
                double d;
                d = (Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()) ? _livingEntity0.getAttribute(CAAttributes.LIVING_BARRIER.get()).getBaseValue() : 0;
                if (d > 0) {
                    barrier = new java.text.DecimalFormat("##.#").format(d);
                }
            }
            if (!defense.isEmpty()){
				event.getGuiGraphics().blit(DEFENSE, 4+dx, h - 24+dy, 0, 0, 9, 9, 9, 9);
				event.getGuiGraphics().drawString(font, defense, 13+dx, h - 25+dy, -16777216, false);
				event.getGuiGraphics().drawString(font, defense, 14+dx, h - 25+dy, -1, false);
			}
			if (!resis.isEmpty()){
				event.getGuiGraphics().blit(RESIS, 4+dx, h - 13+dy, 0, 0, 9, 9, 9, 9);
                double result = 0;
                if (entity != null) {
                    double d;
                    d = (Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()) ? _livingEntity0.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getValue() : 0;
                    result = d * 0.25;
                }
                int len = (int) result;
				event.getGuiGraphics().blit(RESIS_BAR, 13+dx, h - 8+dy, 0, 0, 25, 3, 25, 6);
				event.getGuiGraphics().blit(RESIS_BAR, 13+dx, h - 8+dy, 0, 3, len, 3, 25, 6);
				event.getGuiGraphics().drawString(font, resis, 15+dx, h - 14+dy, -16777216, false);
				event.getGuiGraphics().drawString(font, resis, 14+dx, h - 14+dy, -1, false);
			}
			if (!miss.isEmpty()){
				event.getGuiGraphics().blit(MISS, 42+dx, h - 22+dy, 0, 0, 16, 6, 16, 6);
				event.getGuiGraphics().drawString(font, miss, 60+dx, h - 25+dy, -16777216, false);
				event.getGuiGraphics().drawString(font, miss, 59+dx, h - 25+dy, -1, false);
			}
			if (!barrier.isEmpty()){
				event.getGuiGraphics().blit(BARRIER, 42+dx, h - 13+dy, 0, 0, 9, 9, 9, 9);
                double result;
                if (entity == null) {
                    result = 0;
                } else {
                    double d;
                    double h1;
                    d = (Entity) entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.LIVING_BARRIER.get()) ? _livingEntity0.getAttribute(CAAttributes.LIVING_BARRIER.get()).getBaseValue() : 0;
                    h1 = (Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1;
                    if (h1 <= 0) {
                        result = 25;
                    } else {
                        d = d / h1;
                        if (d <= 1) {
                            result = d * 25;
                        } else {
                            result = Math.min((1 + Math.log10(d)) * 25, 50);
                        }
                    }
                }
                int len = (int) result;
				event.getGuiGraphics().blit(BARRIER_BAR, 51+dx, h - 7+dy, 0, 3, 25, 3, 50, 6);
				event.getGuiGraphics().blit(BARRIER_BAR, 51+dx, h - 8+dy, 0, 0, len, 3, 50, 6);
				event.getGuiGraphics().drawString(font, barrier, 52+dx, h - 14+dy, -16777216, false);		
				event.getGuiGraphics().drawString(font, barrier, 51+dx, h - 14+dy, -1, false);
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
