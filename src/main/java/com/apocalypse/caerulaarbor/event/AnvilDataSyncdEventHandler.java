package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.init.CAItems;
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
			{
				CompoundTag _nbtTag = leftItem.getTag();
				if (_nbtTag != null)
					output.setTag(_nbtTag.copy());
			}
		}
	}

}
