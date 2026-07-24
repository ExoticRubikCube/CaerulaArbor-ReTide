package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.datagen.registry.LootModifiersRegistry;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * additem 全局战利品修改器
 */
public class LootTableModifier extends LootModifier {
    /**
     * additem 序列化器 codec
     */
    public static final Supplier<Codec<LootTableModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(ResourceLocation.CODEC.optionalFieldOf("lootTable").forGetter(modifier -> Optional.ofNullable(modifier.lootTable)))
                    .and(ItemStack.CODEC.optionalFieldOf("item").forGetter(modifier -> Optional.ofNullable(modifier.itemStack)))
                    .apply(instance, (conditions, lootTable, itemStack) -> new LootTableModifier(
                            conditions,
                            lootTable.orElse(null),
                            itemStack.orElse(null)
                    )))
    );

    private final @Nullable ResourceLocation lootTable;
    private final @Nullable ItemStack itemStack;

    private LootTableModifier(LootItemCondition[] conditions, @Nullable ResourceLocation lootTable, @Nullable ItemStack itemStack) {
        super(conditions);
        if ((lootTable == null) == (itemStack == null)) {
            throw new IllegalArgumentException("LootTableModifier needs exactly one of lootTable or item");
        }
        if (itemStack != null && itemStack.isEmpty()) {
            throw new IllegalArgumentException("LootTableModifier item cannot be empty");
        }
        this.lootTable = lootTable;
        this.itemStack = itemStack == null ? null : itemStack.copy();
    }

    /**
     * 构造追加战利品表的修改器
     *
     * @param conditions 触发条件
     * @param lootTable  要追加的战利品表 ID
     * @return 战利品修改器实例
     */
    public static LootTableModifier build(LootItemCondition[] conditions, ResourceLocation lootTable) {
        return new LootTableModifier(conditions, lootTable, null);
    }

    /**
     * 构造追加固定物品栈的修改器
     *
     * @param conditions 触发条件
     * @param itemStack  要追加的物品栈
     * @return 战利品修改器实例
     */
    public static LootTableModifier build(LootItemCondition[] conditions, ItemStack itemStack) {
        return new LootTableModifier(conditions, null, itemStack);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (lootTable != null) {
            context.getResolver().getLootTable(lootTable).getRandomItems(context, generatedLoot::add);
        }
        if (itemStack != null) {
            generatedLoot.add(itemStack.copy());
        }
        return generatedLoot;
    }

    /**
     * 返回 additem 序列化器 codec
     */
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return LootModifiersRegistry.ADDITEM.get();
    }
}
