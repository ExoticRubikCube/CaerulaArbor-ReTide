package com.susen36.caerulaarbor.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.susen36.babel.BabelMod;
import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.difficulty.NDifficulty;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.manager.EPManager;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.menu.CaerulaRecordGUIMenu;
import com.susen36.caerulaarbor.network.send.CaerulaRecordGUIButtonMessage;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import com.susen36.caerulaarbor.util.RecordColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.WidgetSprites;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.text.DecimalFormat;
import java.util.HashMap;

public abstract class CaerulaRecordGUIScreen extends AbstractContainerScreen<CaerulaRecordGUIMenu> {
	private final static HashMap<String, Object> guistate = CaerulaRecordGUIMenu.guistate;
	private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.##");
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_show_on_hud;
	ImageButton imagebutton_relic_icon;

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

	public abstract RecordColor recordColor();

	public abstract String themeKey();

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics,mouseX,mouseY,partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Entity result1 = null;
        if (entity != null) {
            result1 = entity;
        }
        if (result1 instanceof LivingEntity livingEntity) {
			InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.leftPos + 39, this.topPos + 4, this.leftPos + 126, this.topPos + 75, 30, 0.0f, mouseX, mouseY, livingEntity);
		}
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 4 && mouseX < leftPos + 28 && mouseY > topPos + 123 && mouseY < topPos + 147)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_occupy_warn"), mouseX, mouseY);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_ATTENTION))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_discon"), mouseX, mouseY);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_BLOOD))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_haemp"), mouseX, mouseY);
		if (mouseX > leftPos + 6 && mouseX < leftPos + 22 && mouseY > topPos + 99 && mouseY < topPos + 115)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_show_relics"), mouseX, mouseY);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_NEURO))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_neuro"), mouseX, mouseY);
		if (mouseX > leftPos + 5 && mouseX < leftPos + 16 && mouseY > topPos + 32 && mouseY < topPos + 43)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_playerlife"), mouseX, mouseY);
		if (mouseX > leftPos + 5 && mouseX < leftPos + 16 && mouseY > topPos + 53 && mouseY < topPos + 64)
			guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_playershield"), mouseX, mouseY);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_FLESH))
			if (mouseX > leftPos + 101 && mouseX < leftPos + 173 && mouseY > topPos + 147 && mouseY < topPos + 163)
				guiGraphics.renderTooltip(font, Component.translatable("gui.caerula_arbor.caerula_record_gui.tooltip_flesh"), mouseX, mouseY);
		if (mouseX > leftPos + 136 && mouseX < leftPos + 161 && mouseY > topPos + 7 && mouseY < topPos + 27) {
            String result;
            if ((ModCapabilities.getPlayerVariables(entity)).player_oceanization == 3) {
                result = Component.translatable("item.caerula_arbor.language_key.description_13").getString();
            } else if ((ModCapabilities.getPlayerVariables(entity)).player_oceanization == 2) {
                result = Component.translatable("item.caerula_arbor.language_key.description_12").getString();
            } else if ((ModCapabilities.getPlayerVariables(entity)).player_oceanization == 1) {
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

		int recordColor = recordColor().getMainColor();
		RenderSystem.setShaderColor(((recordColor >> 16) & 0xFF) / 255.0F, ((recordColor >> 8) & 0xFF) / 255.0F, (recordColor & 0xFF) / 255.0F, 1.0F);
		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/caerularecord__gray.png"), this.leftPos, this.topPos, 0, 0, 168, 166, 168, 166);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/target_health.png"), this.leftPos + 4, this.topPos + 30, 0, 0, 24, 16, 24, 16);

		guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/target_shield.png"), this.leftPos + 4, this.topPos + 50, 0, 0, 24, 16, 24, 16);

		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_ATTENTION)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_attention.png"), this.leftPos + 96, this.topPos + 91, 0, 0, 64, 64, 64, 64);
		}
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_BLOOD)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_blood.png"), this.leftPos + 101, this.topPos + 90, 0, 0, 64, 64, 64, 64);
		}
		if (PlayerStateUtils.isLightBright(entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightWaving(entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_waving.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightDim(entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_dim.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}
		if (PlayerStateUtils.isLightCeased(entity)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/overlay/light_extinguish.png"), this.leftPos + 36, this.topPos + -37, 0, 0, 64, 32, 64, 32);
		}

		AbstractEPCapability currentElement = EPManager.getEP(entity).getCurrentElement();
		if (currentElement != null) {
			double elementValue = currentElement.getValue();
			double maxElementValue = currentElement.getMaxValue();
			ResourceLocation elementRing = ResourceLocation.fromNamespaceAndPath(BabelMod.MODID,
					"textures/gui/ep/" + currentElement.getType().getTextureName() + ".png");
			guiGraphics.blit(elementRing, this.leftPos + 106, this.topPos + 43,
					Mth.clamp(Mth.ceil(elementValue / maxElementValue * 20.0) * 16, 0, 304), 0, 16, 16, 320, 16);
		}

		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_NEURO)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_neuro.png"), this.leftPos + 101, this.topPos + 90, 0, 0, 64, 64, 64, 64);
		}
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_FLESH)) {
			guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/disoclution_flesh.png"), this.leftPos + 99, this.topPos + 92, 0, 0, 64, 64, 64, 64);
		}

        double result = 0;
        result = ModCapabilities.getPlayerVariables(entity).player_oceanization;
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/oceanize_icon.png"), this.leftPos + 137, this.topPos + 7, Mth.clamp((int) result * 24, 0, 72), 0, 24, 20, 96, 20);

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
		guiGraphics.drawString(this.font, HEALTH_FORMAT.format(entity.getHealth()) + "/" + HEALTH_FORMAT.format(entity.getMaxHealth()), 82, 71, -10092442, false);
		guiGraphics.drawString(this.font, HEALTH_FORMAT.format(entity.getHealth()) + "/" + HEALTH_FORMAT.format(entity.getMaxHealth()), 81, 71, -1, false);
		guiGraphics.drawString(this.font, EntityUtils.getLives(entity), 18, 33, -16764058, false);
		guiGraphics.drawString(this.font, EntityUtils.getLives(entity), 17, 33, -1, false);
		guiGraphics.drawString(this.font, EntityUtils.getLiveMaxShown(entity), 36, 33, -16764058, false);
		guiGraphics.drawString(this.font, EntityUtils.getLiveMaxShown(entity), 35, 33, -16724737, false);
		guiGraphics.drawString(this.font, EntityUtils.getShield(entity), 18, 53, -13421773, false);
		guiGraphics.drawString(this.font, EntityUtils.getShield(entity), 17, 53, -1, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_discolution"), 102, 86, -13434829, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_disoclution"), 101, 86, -3368449, false);
        String result = "";
        result = new DecimalFormat("##.##").format((ModCapabilities.getPlayerVariables(entity)).player_light);
        guiGraphics.drawString(this.font, result, 100, -13, -1, false);
		if (PlayerStateUtils.isLightBright(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsablaze"), 100, -29, -3342337, false);
		if (PlayerStateUtils.isLightWaving(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsflickering"), 100, -29, -3342388, false);
		if (PlayerStateUtils.isLightDim(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightsdim"), 100, -29, -13159, false);
		if (PlayerStateUtils.isLightCeased(entity))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_lightstranquil"), 100, -29, -26215, false);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_ATTENTION))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_disconcentration"), 101, 147, -3368449, false);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_BLOOD))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_haemophilia"), 101, 147, -3368449, false);
		AbstractEPCapability currentElement = EPManager.getEP(entity).getCurrentElement();
		if (currentElement != null) {
			String elementValue = Math.round(currentElement.getValue()) + "/" + Math.round(currentElement.getMaxValue());
			guiGraphics.drawString(this.font,
					elementValue, 124, 50, -16737895, false);
			guiGraphics.drawString(this.font,
					elementValue, 123, 50, -1, false);
			guiGraphics.drawString(this.font, currentElement.getType().description(), 124, 41, -16737895, false);
			guiGraphics.drawString(this.font, currentElement.getType().description(), 123, 41, -1, false);
		}
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_NEURO))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_neurodegression"), 101, 147, -3368449, false);
		if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.DISO_FLESH))
			guiGraphics.drawString(this.font, Component.translatable("gui.caerula_arbor.caerula_record_gui.label_deformity"), 101, 147, -3368449, false);
        if (NDifficulty.difficultyLevel(world).isNormal())
            guiGraphics.drawString(this.font, Component.translatable("key.surging_waves").getString() + "·" + NDifficulty.difficultyLevel(world).value(), -4, -13, -10040065, false);
	}

	@Override
	public void init() {
		super.init();
		button_show_on_hud = new PlainTextButton(this.leftPos + 4, this.topPos + 123, 82, 20, Component.translatable(themeKey()), e -> {
            PacketDistributor.sendToServer(new CaerulaRecordGUIButtonMessage(0, x, y, z));
            CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
        }, this.font);
		guistate.put("button:button_show_on_hud", button_show_on_hud);
		this.addRenderableWidget(button_show_on_hud);
		imagebutton_relic_icon = new ImageButton(this.leftPos + 6, this.topPos + 99, 16, 16, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "screen/atlas/imagebutton_relic_icon"), ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "screen/atlas/imagebutton_relic_icon_highlighted")), e -> {
            PacketDistributor.sendToServer(new CaerulaRecordGUIButtonMessage(1, x, y, z));
            CaerulaRecordGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
        }) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
				if (!this.visible) {
					return;
				}
				int iconColor = recordColor().getSubIconColor();
				RenderSystem.setShaderColor(((iconColor >> 16) & 0xFF) / 255.0F, ((iconColor >> 8) & 0xFF) / 255.0F, (iconColor & 0xFF) / 255.0F, 1.0F);
				guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/gui/screen/relic_icon__gray.png"), this.getX(), this.getY(), 0, 0, 16, 16, 16, 16);
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}
		};
		guistate.put("button:imagebutton_relic_icon", imagebutton_relic_icon);
		this.addRenderableWidget(imagebutton_relic_icon);
	}
}