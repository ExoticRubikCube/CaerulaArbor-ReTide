package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.init.CALootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootTableIdCondition;

/**
 * 生成全局战利品修改器数据。
 */
public class GlobalLootModifierProvider extends net.minecraftforge.common.data.GlobalLootModifierProvider {
    /**
     * 创建全局战利品修改器 provider。
     *
     * @param output datagen 输出位置
     * @param modid  输出所属命名空间
     */
    public GlobalLootModifierProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    private static CALootModifier.CaerulaArborModLootTableModifier appendLootTable(String targetLootTable, String appendedLootTable) {
        return new CALootModifier.CaerulaArborModLootTableModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(resLoc(targetLootTable)).build()
                },
                resLoc(appendedLootTable)
        );
    }

    private static ResourceLocation resLoc(String id) {
        var separator = id.indexOf(':');
        if (separator >= 0) {
            return ResourceLocation.fromNamespaceAndPath(id.substring(0, separator), id.substring(separator + 1));
        }
        return ResourceLocation.withDefaultNamespace(id);
    }

    /**
     * 注册所有全局战利品修改器。
     */
    @Override
    protected void start() {
        add("get_hot_kettle", appendLootTable(
                "chests/spawn_bonus_chest",
                "caerula_arbor:chests/spawn_bonus_appendix"
        ));
        add("template_spawn", appendLootTable(
                "chests/shipwreck_supply",
                "caerula_arbor:chests/shipwreck_map"
        ));
        add("template_spawn_1", appendLootTable(
                "chests/shipwreck_treasure",
                "caerula_arbor:chests/shipwreck_map"
        ));
        add("template_spawn_2", appendLootTable(
                "chests/underwater_ruin_big",
                "caerula_arbor:chests/shipwreck_map"
        ));
        add("template_spawn_3", appendLootTable(
                "chests/underwater_ruin_small",
                "caerula_arbor:chests/shipwreck_map"
        ));
    }
}
