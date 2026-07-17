package com.apocalypse.caerulaarbor.recipe;

import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FuelsBurnHandler {
    @SubscribeEvent
    public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        ItemStack itemstack = event.getItemStack();
        if (itemstack.getItem() == CAItems.SEA_TRAIL_MOR.get())
            event.setBurnTime(200);
        else if (itemstack.getItem() == CABlocks.SEA_TRAIL_SOLID.get().asItem())
            event.setBurnTime(2000);
        else if (itemstack.getItem() == CABlocks.TRAIL_LOG.get().asItem())
            event.setBurnTime(400);
        else if (itemstack.getItem() == CABlocks.TRAIL_PLANK.get().asItem())
            event.setBurnTime(400);
        else if (itemstack.getItem() == CABlocks.TRAIL_PLANK_SLAB.get().asItem())
            event.setBurnTime(200);
        else if (itemstack.getItem() == CABlocks.TRAIL_PLANK_STAIR.get().asItem())
            event.setBurnTime(400);
        else if (itemstack.getItem() == CABlocks.TRAIL_PLANKS_FENCE.get().asItem())
            event.setBurnTime(400);
        else if (itemstack.getItem() == CABlocks.TRAIL_PLANK_FENCEDOOR.get().asItem())
            event.setBurnTime(400);
        else if (itemstack.getItem() == CABlocks.STRIPPED_TRAIL_LOG.get().asItem())
            event.setBurnTime(400);
    }
}
