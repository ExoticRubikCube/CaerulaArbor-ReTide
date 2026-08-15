package com.susen36.caerulaarbor.client.gui.screen;

import com.susen36.caerulaarbor.menu.CaerulaRecordGUIMenu;
import com.susen36.caerulaarbor.util.RecordColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RecordCaerulaScreen extends CaerulaRecordGUIScreen {
	public RecordCaerulaScreen(CaerulaRecordGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
	}

	@Override
	public RecordColor recordColor() {
		return RecordColor.DEEPBLUE;
	}

	@Override
	public String themeKey() {
		return "gui.caerula_arbor.caerula_record_gui.button_theme_deepblue";
	}
}