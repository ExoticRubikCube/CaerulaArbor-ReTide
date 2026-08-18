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
			// 进化玩家使用金色背景贴图且不显示阶段图标；半海嗣玩家使用蓝色背景与蓝色图标；其他正常显示
			boolean evolved = ModCapabilities.getPlayerVariables(entity).can_player_evo;
			boolean semiOceanized = ModCapabilities.getPlayerVariables(entity).player_oceanization >= 1 && ModCapabilities.getPlayerVariables(entity).player_oceanization < 3;
			// 进化与满海嗣化(>=3)均不显示阶段图标
			if (rejectionStage != 0) {
				boolean suppressIcon = evolved || ModCapabilities.getPlayerVariables(entity).player_oceanization >= 3;
				event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, evolved ? "textures/gui/overlay/transforming_gold.png" : (semiOceanized ? "textures/gui/overlay/transforming_blue.png" : "textures/gui/overlay/transforming0.png")), w - 64, h - 128, 0, 0, 64, 128, 64, 128);
				if (!suppressIcon) {
					int regionX = w - 4 - 64;
					int regionY = h - 12 - 32;
					String baseIcon = semiOceanized ? "textures/gui/screen/disoclution_combos/disoclution_" : "";
					if (rejectionStage == 1) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "attention_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_attention.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 2) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 3) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "neuro_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_neuro.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 4) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 5) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_attention_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_attention.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 6) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 7) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 8) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_attention_blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_attention_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 9) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_attention_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_attention_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 10) {
						event.getGuiGraphics().blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_blood_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_blood_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					}
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