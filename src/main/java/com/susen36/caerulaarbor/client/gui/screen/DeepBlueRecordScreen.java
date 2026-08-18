package com.susen36.caerulaarbor.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.menu.CaerulaRecordGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class DeepBlueRecordScreen extends RecordGUIScreen {
	public DeepBlueRecordScreen(CaerulaRecordGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/caerularecord.png");
	}

	@Override
	public ResourceLocation getIconTexture() {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/relic_icon.png");
	}

	@Override
	public String themeKey() {
		return "gui.caerula_arbor.caerula_record_gui.button_theme_parchment";
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		super.renderBg(guiGraphics, partialTicks, gx, gy);

		Player player = Minecraft.getInstance().player;
		if (player != null) {
			// 深蓝主题在界面右下角绘制排异反应背景与阶段图标，判定与 HUD 一致
			double rejectionStage = ModCapabilities.getPlayerVariables(player).disoclusion;
			if (rejectionStage != 0) {
				boolean evolved = ModCapabilities.getPlayerVariables(player).can_player_evo;
				boolean semiOceanized = ModCapabilities.getPlayerVariables(player).player_oceanization >= 1
						&& ModCapabilities.getPlayerVariables(player).player_oceanization < 3;
				boolean suppressIcon = evolved || ModCapabilities.getPlayerVariables(player).player_oceanization >= 3;

				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.setShaderColor(1, 1, 1, 1);
				int rightX = this.leftPos + 168 - 64;
				int rightY = this.topPos + 166 - 128;
				guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, evolved ? "textures/gui/overlay/transforming_gold.png" : (semiOceanized ? "textures/gui/overlay/transforming_blue.png" : "textures/gui/overlay/transforming0.png")), rightX, rightY, 0, 0, 64, 128, 64, 128);
				if (!suppressIcon) {
					int regionX = rightX + 64 - 4 - 64;
					int regionY = rightY + 128 - 8 - 32;
					String baseIcon = semiOceanized ? "textures/gui/screen/disoclution_combos/disoclution_" : "";
					if (rejectionStage == 1) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "attention_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_attention.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 2) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 3) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "neuro_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_neuro.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 4) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 5) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_attention_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_attention.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 6) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 7) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_neuro_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_neuro_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 8) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_attention_blood_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_attention_blood.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 9) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_attention_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_attention_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					} else if (rejectionStage == 10) {
						guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, semiOceanized ? baseIcon + "combo_blood_flesh_blue.png" : "textures/gui/screen/disoclution_combos/disoclution_combo_blood_flesh.png"), regionX, regionY, 64, 32, 0, 0, 64, 32, 64, 32);
					}
				}
				RenderSystem.disableBlend();
			}
		}
	}
}