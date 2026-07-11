package com.apocalypse.caerulaarbor.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class GlobalLootModifierProvider extends net.minecraftforge.common.data.GlobalLootModifierProvider {
    public GlobalLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    protected void start() {
        this.add(
                "lootable_woodland_mansion",
                LootTableModifier.build(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/spawn_bonus_chest")).build(),
                                LootItemRandomChanceCondition.randomChance(0.10f).build() // 10% 概率
                        },
                        ItemStack.EMPTY
                )
        );
    }

}
