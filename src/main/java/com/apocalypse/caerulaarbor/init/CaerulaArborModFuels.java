/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CaerulaArborModFuels {
	@SubscribeEvent
	public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack itemstack = event.getItemStack();
		if (itemstack.getItem() == CaerulaArborModItems.SEA_TRAIL_MOR.get())
			event.setBurnTime(200);
		else if (itemstack.getItem() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get().asItem())
			event.setBurnTime(2000);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_LOG.get().asItem())
			event.setBurnTime(400);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_PLANK.get().asItem())
			event.setBurnTime(400);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_PLANK_SLAB.get().asItem())
			event.setBurnTime(200);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_PLANK_STAIR.get().asItem())
			event.setBurnTime(400);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_PLANKS_FENCE.get().asItem())
			event.setBurnTime(400);
		else if (itemstack.getItem() == CaerulaArborModBlocks.TRAIL_PLANK_FENCEDOOR.get().asItem())
			event.setBurnTime(400);
		else if (itemstack.getItem() == CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get().asItem())
			event.setBurnTime(400);
	}
}
