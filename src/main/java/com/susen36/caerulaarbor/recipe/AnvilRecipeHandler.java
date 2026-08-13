package com.susen36.caerulaarbor.recipe;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

@EventBusSubscriber
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

	@SubscribeEvent
	public static void onItemTakenFromAnvil(AnvilRepairEvent event) {
		ItemStack leftItem = event.getLeft();
		ItemStack output = event.getOutput();
		if (event.getRight().getItem() == CAItems.KNIGHT_CORPSE.get() && leftItem.getItem() == Items.IRON_SWORD) {
			CompoundTag nbtTag = leftItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			if (!nbtTag.isEmpty())
				CustomData.update(DataComponents.CUSTOM_DATA, output, tag -> tag.merge(nbtTag));
		}
	}
}