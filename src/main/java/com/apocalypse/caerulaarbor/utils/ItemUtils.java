package com.apocalypse.caerulaarbor.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {

	private ItemUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String getCursedDescription(ItemStack itemstack) {
		String first_two = "";
		String locId = "";
		locId = itemstack.getDescriptionId();
		first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString();
		if (itemstack.getOrCreateTag().getBoolean("used")) {
			return first_two + "\n" + Component.translatable("item.caerula_arbor.cursed.used").getString();
		}
		return first_two;
	}

	public static boolean isFilledwithPersonnel(ItemStack itemstack) {
		String name = itemstack.getOrCreateTag().getString("name");
		if ((name).equals("apocata")) {
			return false;
		}
		return !(name).isEmpty();
	}

	public static String getOneUseItemDescription(ItemStack itemstack) {
		String locId = itemstack.getDescriptionId();
		String first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString();
		if (itemstack.getOrCreateTag().getBoolean("used")) {
			return first_two + "\n" + Component.translatable("item.caerula_arbor.relics.used").getString();
		}
		return first_two;
	}
}
