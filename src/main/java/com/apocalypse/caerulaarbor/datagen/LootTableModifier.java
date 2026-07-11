package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.datagen.registry.LootModifiersRegistry;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LootTableModifier extends LootModifier {
    public static final Supplier<Codec<LootTableModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst ->
                    codecStart(inst).apply(inst, LootTableModifier::new)
            )
    );
    private static LootTable lootTable;
    private static ItemStack itemStack;

    private LootTableModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    /**
     * 构造函数1：使用LootTable（概率包含于LootTable）
     * LootItemCondition[]应该为长度为1的数组
     *
     * @param wantedLootTable 意欲添加的LootTable
     */
    public static LootTableModifier build(LootItemCondition[] conditions, LootTable wantedLootTable) {
        if (conditions.length != 1)
            throw new IllegalArgumentException("LootItemCondition[] should just has 1 object in it, but found " + conditions.length + (conditions.length == 0 ? "object" : "objects"));
        lootTable = wantedLootTable;
        return new LootTableModifier(conditions);
    }

    /**
     * 构造函数1：使用ItemStack（概率包含于LootItemCondition[1] --> chance）
     * LootItemCondition[]应该为长度为2的数组
     *
     * @param wantedItemStack 意欲添加的ItemStack
     */
    public static LootTableModifier build(LootItemCondition[] conditions, ItemStack wantedItemStack) {
        if (conditions.length != 2)
            throw new IllegalArgumentException("LootItemCondition[] should just has 2 object in it, but found " + conditions.length + (conditions.length <= 1 ? "object" : "objects"));
        itemStack = wantedItemStack;
        return new LootTableModifier(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (lootTable != null) {
            //TODO :  loottable
        } else {
            generatedLoot.add(itemStack);
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return LootModifiersRegistry.ADDITEM.get();
    }
}
