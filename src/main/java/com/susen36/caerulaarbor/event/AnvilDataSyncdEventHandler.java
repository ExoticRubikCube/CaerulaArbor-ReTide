package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AnvilDataSyncdEventHandler {
	@SubscribeEvent
	public static void onItemTakenFromAnvil(AnvilRepairEvent event) {
		ItemStack leftItem = event.getLeft();
		ItemStack output = event.getOutput();
		if (event.getRight().getItem() == CAItems.KNIGHT_CORPSE.get() && leftItem.getItem() == Items.IRON_SWORD) {
			CompoundTag nbtTag = leftItem.getTag();
			if (nbtTag != null)
				output.setTag(nbtTag.copy());
		}
	}

}
