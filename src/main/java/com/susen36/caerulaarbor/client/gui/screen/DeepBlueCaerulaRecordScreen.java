package com.susen36.caerulaarbor.client.gui.screen;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.menu.CaerulaRecordGUIMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DeepBlueCaerulaRecordScreen extends CaerulaRecordGUIScreen {
	public DeepBlueCaerulaRecordScreen(CaerulaRecordGUIMenu container, Inventory inventory, Component text) {
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
}