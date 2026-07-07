package com.apocalypse.caerulaarbor.client.gui;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAGameRules;
import com.apocalypse.caerulaarbor.init.CANetwork;
import com.apocalypse.caerulaarbor.menu.CaerulaRecordGUIMenu;
import com.apocalypse.caerulaarbor.network.send.CaerulaRecordGUIButtonMessage;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.PlayerStateUtils;
import com.apocalypse.caerulaarbor.util.RelicUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashMap;

public class CaerulaRecordGUIScreen extends AbstractContainerScreen<CaerulaRecordGUIMenu> {
	private final static HashMap<String, Object> guistate = CaerulaRecordGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_show_on_hud;
	Button button_show_relic_ptc;
	ImageButton imagebutton_relic_icon;
	ImageButton imagebutton_nurture_gene_set;

	public CaerulaRecordGUIScreen(CaerulaRecordGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 168;
		this.imageHeight = 166;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Entity result1 = null;
        if (entity != null) {
            result1 = entity;
        }
        if (result1 instanceof LivingEntity livingEntity) {
			InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + 86, this.topPos + 67, 30, 0f + (float) Math.atan((this.leftPos + 86 - mouseX) / 40.0), (float) Math.atan((this.topPos + 18 - mouseY) / 40.0), livingEntity);
		}
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 4 && mouseX < leftPos + 28 && mouseY > topPos + 123 && mouseY < topPos + 147)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_occupy_warn"), mouseX, mouseY);
		if (RelicUtils.hasDisoAttention(entity))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_discon"), mouseX, mouseY);
		if (RelicUtils.hasDisoBlood(entity))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_haemp"), mouseX, mouseY);
		if (mouseX > leftPos + 6 && mouseX < leftPos + 22 && mouseY > topPos + 99 && mouseY < topPos + 115)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_show_relics"), mouseX, mouseY);
		if (RelicUtils.hasDisoNeuro(entity))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_neuro"), mouseX, mouseY);
		if (mouseX > leftPos + 5 && mouseX < leftPos + 16 && mouseY > topPos + 32 && mouseY < topPos + 43)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_playerlife"), mouseX, mouseY);
		if (mouseX > leftPos + 5 && mouseX < leftPos + 16 && mouseY > topPos + 53 && mouseY < topPos + 64)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_playershield"), mouseX, mouseY);
		if (RelicUtils.hasDisoFlesh(entity))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_flesh"), mouseX, mouseY);
		if (mouseX > leftPos + 136 && mouseX < leftPos + 161 && mouseY > topPos + 7 && mouseY < topPos + 27) {
            String result;
            if (entity == null) {
                result = "";
            } else if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization == 3) {
                result = Component.translatable("item.caerula_arbor.language_key.description_13").getString();
            } else if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization == 2) {
                result = Component.translatable("item.caerula_arbor.language_key.description_12").getString();
            } else if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization == 1) {
                result = Component.translatable("item.caerula_arbor.language_key.description_11").getString();
            } else {
                result = Component.translatable("item.caerula_arbor.language_key.description_10").getString();
            }
            guiGraphics.renderTooltip(font, Component.literal(result), mouseX, mouseY);
        }
		if (PlayerStateUtils.canPlayerEvo(entity))
			if (mouseX > leftPos + 28 && mouseX < leftPos + 44 && mouseY > topPos + 99 && mouseY < topPos + 115)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_playerevolution"), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/caerularecord.png"), this.leftPos, this.topPos, 0, 0, 168, 166, 168, 166);

		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/target_health.png"), this.leftPos + 4, this.topPos + 30, 0, 0, 24, 16, 24, 16);

		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/target_shield.png"), this.leftPos + 4, this.topPos + 50, 0, 0, 24, 16, 24, 16);

		if (RelicUtils.hasDisoAttention(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/disoclution_attention.png"), this.leftPos + 96, this.topPos + 91, 0, 0, 64, 64, 64, 64);
		}
		if (RelicUtils.hasDisoBlood(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/disoclution_blood.png"), this.leftPos + 101, this.topPos + 90, 0, 0, 64, 64, 64, 64);
		}
		if (PlayerStateUtils.isLightBright(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/light.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightWaving(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/light_waving.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightDim(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/light_dim.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightCeased(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/light_extinguish.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}

		double sanity = ModCapabilities.getSanityInjury(entity).getValue();
		double maxSanity = ModCapabilities.getSanityInjury(entity).getMaxValue();
		guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/sanity.png"), this.leftPos + 106, this.topPos + 43,
				Mth.clamp((int) Math.ceil(sanity / maxSanity * 20.0) * 16, 0, 304), 0, 16, 16, 320, 16);

		if (RelicUtils.hasDisoNeuro(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/disoclution_neuro.png"), this.leftPos + 101, this.topPos + 90, 0, 0, 64, 64, 64, 64);
		}
		if (RelicUtils.hasDisoFlesh(entity)) {
			guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/disoclution_flesh.png"), this.leftPos + 99, this.topPos + 92, 0, 0, 64, 64, 64, 64);
		}

        double result = 0;
        if (entity != null) {
            result = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization;
        }
        guiGraphics.blit(new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/oceanize_icon.png"), this.leftPos + 137, this.topPos + 7, Mth.clamp((int) result * 24, 0, 72), 0, 24, 20, 96, 20);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_health1"), 45, 71, -10092442, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_health"), 44, 71, -1, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getHealth(entity), 82, 71, -10092442, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getHealth(entity), 81, 71, -1, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getLives(entity), 18, 33, -16764058, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getLives(entity), 17, 33, -1, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getLiveMaxShown(entity), 36, 33, -16764058, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getLiveMaxShown(entity), 35, 33, -16724737, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getShield(entity), 18, 53, -13421773, false);
		guiGraphics.drawString(this.font,

				EntityUtils.getShield(entity), 17, 53, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_discolution"), 102, 86, -13434829, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_disoclution"), 101, 86, -3368449, false);
        String result = "";
        if (entity != null) {
            result = new java.text.DecimalFormat("##.##").format((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light);
        }
        guiGraphics.drawString(this.font,

                result, 100, -13, -1, false);
		if (PlayerStateUtils.isLightBright(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsablaze"), 100, -29, -3342337, false);
		if (PlayerStateUtils.isLightWaving(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsflickering"), 100, -29, -3342388, false);
		if (PlayerStateUtils.isLightDim(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsdim"), 100, -29, -13159, false);
		if (PlayerStateUtils.isLightCeased(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightstranquil"), 100, -29, -26215, false);
		if (RelicUtils.hasDisoAttention(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_disconcentration"), 101, 147, -3368449, false);
		if (RelicUtils.hasDisoBlood(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_haemophilia"), 101, 147, -3368449, false);
		String sanity = Math.round(ModCapabilities.getSanityInjury(entity).getValue()) + "/" + Math.round(ModCapabilities.getSanityInjury(entity).getMaxValue());
		guiGraphics.drawString(this.font,

				sanity, 124, 50, -16737895, false);
		guiGraphics.drawString(this.font,

				sanity, 123, 50, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_sanity1"), 124, 41, -16737895, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_sanity"), 123, 41, -1, false);
		if (RelicUtils.hasDisoNeuro(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_neurodegression"), 101, 147, -3368449, false);
		if (RelicUtils.hasDisoFlesh(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_deformity"), 101, 147, -3368449, false);
        if ((((LevelAccessor) world).getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES)) > 0)
            guiGraphics.drawString(this.font,

                    Component.translatable("key.surging_waves").getString() + "\u00B7" + Math.round((((LevelAccessor) world).getLevelData().getGameRules().getInt(CAGameRules.SURGING_WAVES))), -4, -13, -10040065, false);
	}

	@Override
	public void init() {
		super.init();
		button_show_on_hud = new PlainTextButton(this.leftPos + 4, this.topPos + 123, 82, 20, Component.translatable("gui.caerula_arbor.caerula_record_gui.button_show_on_hud"), e -> {
			if (true) {
				CANetwork.PACKET_HANDLER.sendToServer(new CaerulaRecordGUIButtonMessage(0, x, y, z));
				CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_show_on_hud", button_show_on_hud);
		this.addRenderableWidget(button_show_on_hud);
		button_show_relic_ptc = new PlainTextButton(this.leftPos + 4, this.topPos + 147, 98, 20, Component.translatable("gui.caerula_arbor.caerula_record_gui.button_show_relic_ptc"), e -> {
			if (true) {
				CANetwork.PACKET_HANDLER.sendToServer(new CaerulaRecordGUIButtonMessage(1, x, y, z));
				CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}, this.font);
		guistate.put("button:button_show_relic_ptc", button_show_relic_ptc);
		this.addRenderableWidget(button_show_relic_ptc);
		imagebutton_relic_icon = new ImageButton(this.leftPos + 6, this.topPos + 99, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/atlas/imagebutton_relic_icon.png"), 16, 32, e -> {
			if (true) {
				CANetwork.PACKET_HANDLER.sendToServer(new CaerulaRecordGUIButtonMessage(2, x, y, z));
				CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		});
		guistate.put("button:imagebutton_relic_icon", imagebutton_relic_icon);
		this.addRenderableWidget(imagebutton_relic_icon);
		imagebutton_nurture_gene_set = new ImageButton(this.leftPos + 28, this.topPos + 99, 16, 16, 0, 0, 16, new ResourceLocation(CaerulaArborMod.MODID, "textures/overlay/atlas/imagebutton_nurture_gene_set.png"), 16, 32, e -> {
			if (PlayerStateUtils.canPlayerEvo(entity)) {
				CANetwork.PACKET_HANDLER.sendToServer(new CaerulaRecordGUIButtonMessage(3, x, y, z));
				CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = PlayerStateUtils.canPlayerEvo(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_nurture_gene_set", imagebutton_nurture_gene_set);
		this.addRenderableWidget(imagebutton_nurture_gene_set);
	}
}
