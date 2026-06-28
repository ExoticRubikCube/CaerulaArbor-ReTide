package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SyncNetherseaSwordEventHandler {
	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		ItemStack craftedItem = event.getCrafting();
		ItemStack sourceSword = ItemStack.EMPTY;
		Container inventory = event.getInventory();
		int containerSize = inventory.getContainerSize();
		for (int slot = 0; slot < containerSize; slot++) {
			ItemStack ingredient = inventory.getItem(slot);
			if (ingredient.is(ItemTags.SWORDS) || ingredient.getItem() instanceof SwordItem) {
				sourceSword = ingredient.copy();
				if (slot < containerSize - 1 && inventory.getItem(slot + 1).is(CaerulaArborModItems.TRAIL_CREAM.get())) {
					break;
				}
				return;
			}
		}
		if (sourceSword.isEmpty())
			return;
		CompoundTag swordTag = sourceSword.getTag();
		if (swordTag != null)
			craftedItem.setTag(swordTag.copy());
	}
}
