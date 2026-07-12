package com.apocalypse.caerulaarbor.datagen.registry;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.datagen.LootTableModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 全局战利品修改器序列化器注册表
 */
public class LootModifiersRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, CaerulaArborMod.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADDITEM =
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