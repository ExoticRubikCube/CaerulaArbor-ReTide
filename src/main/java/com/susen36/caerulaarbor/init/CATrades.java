package com.susen36.caerulaarbor.init;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber
public class CATrades {
    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            event.getTrades().get(2).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 8), new ItemStack(Items.PAPER), new ItemStack(CAItems.RESCISSION.get()), 4, 4, 0.02f));
        }
        if (event.getType() == VillagerProfession.TOOLSMITH) {
            event.getTrades().get(3).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 12), new ItemStack(Items.EMERALD, 8), new ItemStack(CAItems.OMNI_KEY.get()), 2, 8, 0.05f));
        }
        if (event.getType() == CAVillagerProfessions.CANNOT_GOODENOUGH.get()) {
            event.getTrades().get(5).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 24), new ItemStack(Blocks.RED_WOOL, 16), new ItemStack(CAItems.ARCHFIENDS_FLAG.get()), 1, 36, 0.04f));
            event.getTrades().get(4).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 16), new ItemStack(Items.WHITE_BED), new ItemStack(CAItems.ARCHFIENDS_BED.get()), 1, 24, 0.04f));
            event.getTrades().get(4).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 16), new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(CAItems.KINGS_EXTENSION.get()), 1, 24, 0.04f));
            event.getTrades().get(5).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 24), new ItemStack(Items.NETHERITE_SCRAP), new ItemStack(CAItems.KINGS_SPEAR.get()), 10, 15, 0.04f));
            event.getTrades().get(1).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 12), new ItemStack(Items.PRISMARINE_SHARD, 16), new ItemStack(CACollectible.ODD_FLUTE.get()), 10, 15, 0.04f));
            event.getTrades().get(3).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 16), new ItemStack(Blocks.GRASS_BLOCK, 32), new ItemStack(CABlocks.REDSTONEIRIS_SEEDING.get()), 4, 24, 0.04f));
            event.getTrades().get(5).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Items.CROSSBOW), new ItemStack(CAItems.HAND_OF_STRANGLE.get()), 1, 36, 0.04f));
            event.getTrades().get(1).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Items.DIAMOND_HOE), new ItemStack(CAItems.HAND_OF_FERTILIY.get()), 1, 36, 0.04f));
            event.getTrades().get(2).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Items.DIAMOND_SWORD), new ItemStack(CACollectible.HAND_SWORD.get()), 1, 36, 0.04f));
            event.getTrades().get(5).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Blocks.STRIPPED_CHERRY_LOG, 32), new ItemStack(CAItems.HAND_OF_BARREN.get()), 1, 36, 0.04f));
            event.getTrades().get(4).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Items.FIREWORK_STAR, 12), new ItemStack(CAItems.HAND_OF_FIREWORK.get()), 1, 36, 0.04f));
            event.getTrades().get(1).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 8), new ItemStack(Blocks.SEAGRASS), new ItemStack(CACollectible.BOWL_SEAGRASS.get()), 4, 8, 0.03f));
            event.getTrades().get(1).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 12), new ItemStack(Items.FERMENTED_SPIDER_EYE, 6), new ItemStack(CAItems.SMELLY_HEMOSTATIC.get()), 3, 24, 0.04f));
            event.getTrades().get(5).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 32), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(CAItems.UNRIPE_YEARNING.get()), 1, 36, 0.04f));
            event.getTrades().get(4).add(new BasicItemListing(new ItemStack(CAItems.REDSTONE_INGOT.get(), 24), new ItemStack(Items.EMERALD), 16, 3, 0.02f));
        }
    }
}