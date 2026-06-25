package com.apocalypse.caerulaarbor.events;

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
public class SyncNetherseaSwordProcedure{
	@SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
    	//CaerulaArborMod.LOGGER.info("Craft event triggered");
        ItemStack output = event.getCrafting();
        ItemStack sword = ItemStack.EMPTY;
        Container inv = event.getInventory();
        int size = inv.getContainerSize();
        for (int i=0; i<size; i++){
            ItemStack tempItem = inv.getItem(i);
            //CaerulaArborMod.LOGGER.info("Found index {}",i);
            if(tempItem.is(ItemTags.SWORDS) || tempItem.getItem() instanceof SwordItem){
                sword = tempItem.copy();
                //CaerulaArborMod.LOGGER.info("Found sword {}",sword.getDisplayName());
                if(i < size-1 && inv.getItem(i+1).is(CaerulaArborModItems.TRAIL_CREAM.get())) break;
                else return;
            }
        }
        if (sword.isEmpty()) return;
        //CaerulaArborMod.LOGGER.info("Sync NBT");
        CompoundTag _nbtTag = sword.getTag();
        if (_nbtTag != null)
            output.setTag(_nbtTag.copy());
    }
}
