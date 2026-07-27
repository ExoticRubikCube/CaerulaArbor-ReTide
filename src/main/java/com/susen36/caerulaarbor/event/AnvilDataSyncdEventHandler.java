package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

@EventBusSubscriber
public class AnvilDataSyncdEventHandler {
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