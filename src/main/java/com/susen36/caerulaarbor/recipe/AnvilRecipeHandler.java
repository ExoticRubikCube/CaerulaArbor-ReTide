package com.susen36.caerulaarbor.recipe;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AnvilRecipeHandler {
	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if ((event.getLeft().getItem() == CAItems.SOLO_MUSIC_BOX.get()) && (event.getRight().getItem() == Items.COPPER_INGOT)) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(4);
				event.setOutput(new ItemStack(CAItems.MUSIC_BOX_FIXED.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_HELMET) && (event.getRight().getItem() == CAItems.RELIC_CROWN.get())) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(5);
				event.setOutput(new ItemStack(CAItems.WEARABLE_CROWN_HELMET.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_CHESTPLATE) && (event.getRight().getItem() == CAItems.KINGS_ARMOUR.get())) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(5);
				event.setOutput(new ItemStack(CAItems.WEARABLE_CHEST_CHESTPLATE.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_SWORD) && (event.getRight().getItem() == CAItems.KNIGHT_CORPSE.get())) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(4);
				event.setOutput(new ItemStack(CAItems.IRON_SWORD_OF_KNIGHT_CORPUS.get()));
			}
		}
	}
}
