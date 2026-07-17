package com.apocalypse.caerulaarbor.init;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CACompostableItems {
    private CACompostableItems() {
    }

    /**
     * 在通用设置阶段注册可堆肥物品
     *
     * @param event 通用设置事件
     */
    public static void addComposterItems(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(CAItems.SEA_TRAIL_MOR.get(), 0.2F);
            ComposterBlock.COMPOSTABLES.put(CABlocks.SEA_TRAIL_SOLID.get().asItem(), 0.75F);
        });
    }
}
