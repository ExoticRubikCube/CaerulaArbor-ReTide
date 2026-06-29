package com.apocalypse.caerulaarbor.client.screens;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class LessArmor1Overlay {
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
            result = (Entity) entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.LESS_ARMOR.get())
                    && ((Entity) entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) > 4
                    && ((Entity) entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) <= 9;
        }
        if (result) {
			event.getGuiGraphics().blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/screens/less_armor_ui.png"), 0, 0, 0, 0, w, h, w, h);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
