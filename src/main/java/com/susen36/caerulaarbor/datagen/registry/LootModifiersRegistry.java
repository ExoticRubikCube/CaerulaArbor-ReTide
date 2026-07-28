package com.susen36.caerulaarbor.datagen.registry;

import com.mojang.serialization.MapCodec;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.datagen.LootTableModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 全局战利品修改器序列化器注册表
 */
public class LootModifiersRegistry {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CaerulaArborMod.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<LootTableModifier>> ADDITEM =
            LOOT_MODIFIER_SERIALIZERS.register(
                    "additem",
                    LootTableModifier.CODEC
            );

    /**
     * 将战利品修改器序列化器注册到 mod 事件总线
     *
     * @param bus mod 事件总线
     */
    public static void register(IEventBus bus) {
        LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}
