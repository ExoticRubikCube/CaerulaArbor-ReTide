/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CACompostableItems {
	@SubscribeEvent
	public static void addComposterItems(FMLCommonSetupEvent event) {
		ComposterBlock.COMPOSTABLES.put(CAItems.SEA_TRAIL_MOR.get(), 0.2f);
		ComposterBlock.COMPOSTABLES.put(CABlocks.SEA_TRAIL_SOLID.get().asItem(), 0.75f);
	}
}
