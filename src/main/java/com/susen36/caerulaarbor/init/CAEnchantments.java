package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 1.21.1 附魔注册：Enchantment 已改为 record，通过 BootstrapContext + Enchantment.definition 注册。
 * <p>原 13 个 extends Enchantment 的类已删除，属性全部迁移至此 bootstrap 方法。</p>
 * <p>cost 公式统一为 getMinCost=1+level*10, getMaxCost=6+level*10，映射为 dynamicCost(11,10) / dynamicCost(16,10)。</p>
 */
public class CAEnchantments {
    public static final ResourceKey<Enchantment> OCEANOSPR_KILLER = key("oceanospr_killer");
    public static final ResourceKey<Enchantment> SANITY_REAPER = key("sanity_reaper");
    public static final ResourceKey<Enchantment> SANITY_DEFEND = key("sanity_defend");
    public static final ResourceKey<Enchantment> REFLECTION = key("reflection");
    public static final ResourceKey<Enchantment> SYNESTHESIA = key("synesthesia");
    public static final ResourceKey<Enchantment> METABOLISM = key("metabolism");
    public static final ResourceKey<Enchantment> MUTE_ATTACK = key("mute_attack");
    public static final ResourceKey<Enchantment> NETHERSEA_WALKER = key("nethersea_walker");
    public static final ResourceKey<Enchantment> FLEXIBILITY = key("flexibility");
    public static final ResourceKey<Enchantment> MAGIC_TOLERANCE = key("magic_tolerance");
    public static final ResourceKey<Enchantment> HAZARD_PROTECTION = key("hazard_protection");
    public static final ResourceKey<Enchantment> SANITY_INJURY_CURSE = key("sanity_injury_curse");
    public static final ResourceKey<Enchantment> REJECTION_CURSE = key("rejection_curse");

    private CAEnchantments() {
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchGetter = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);

        // OCEANOSPR_KILLER — UNCOMMON(5), maxLevel 5, ANY, tag: enchantable/seaborn_killer, 互斥 BANE_OF_ARTHROPODS & SMITE
        register(context, OCEANOSPR_KILLER,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/seaborn_killer")),
                                5, 5,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
                        .exclusiveWith(HolderSet.direct(
                                enchGetter.getOrThrow(Enchantments.BANE_OF_ARTHROPODS),
                                enchGetter.getOrThrow(Enchantments.SMITE)
                        ))
        );

        // SANITY_REAPER — UNCOMMON(5), maxLevel 5, ANY, tag: enchantable/sanity
        register(context, SANITY_REAPER,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity")),
                                5, 5,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
        );

        // SANITY_DEFEND — COMMON(10), maxLevel 4, ARMOR, tag: enchantable/sanity_defend
        register(context, SANITY_DEFEND,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                10, 4,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ARMOR
                        )
                )
        );

        // REFLECTION — COMMON(10), maxLevel 5, ANY, specific items: PHLOEM_BOW, isTradeable=false
        register(context, REFLECTION,
                Enchantment.enchantment(
                        Enchantment.definition(
                                HolderSet.direct(itemGetter.getOrThrow(itemKey("phloem_bow"))),
                                10, 5,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
        );

        // SYNESTHESIA — COMMON(10), maxLevel 5, ANY, specific items: LEGENDARY_SPEAR, HIGHMORE_SCYTHE, DRAGON_WAND
        register(context, SYNESTHESIA,
                Enchantment.enchantment(
                        Enchantment.definition(
                                HolderSet.direct(
                                        itemGetter.getOrThrow(itemKey("legendary_spear")),
                                        itemGetter.getOrThrow(itemKey("highmore_scythe")),
                                        itemGetter.getOrThrow(itemKey("dragon_wand"))
                                ),
                                10, 5,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
        );

        // METABOLISM — COMMON(10), maxLevel 1, ANY, specific items: PHLOEM_BOW, isTreasureOnly=true, isTradeable=false
        register(context, METABOLISM,
                Enchantment.enchantment(
                        Enchantment.definition(
                                HolderSet.direct(itemGetter.getOrThrow(itemKey("phloem_bow"))),
                                10, 1,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
        );

        // MUTE_ATTACK — RARE(2), maxLevel 5, ANY, tag: minecraft:tools
        register(context, MUTE_ATTACK,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "tools"))),
                                2, 5,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
        );

        // NETHERSEA_WALKER — COMMON(10), maxLevel 3, FEET, tag: enchantable/nethersea_walker, 互斥 FROST_WALKER
        register(context, NETHERSEA_WALKER,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/nethersea_walker")),
                                10, 3,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.FEET
                        )
                )
                        .exclusiveWith(HolderSet.direct(enchGetter.getOrThrow(Enchantments.FROST_WALKER)))
        );

        // FLEXIBILITY — UNCOMMON(5), maxLevel 3, ARMOR, tag: enchantable/sanity_defend, 互斥 MAGIC_TOLERANCE
        register(context, FLEXIBILITY,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                5, 3,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ARMOR
                        )
                )
                        .exclusiveWith(HolderSet.direct(enchGetter.getOrThrow(MAGIC_TOLERANCE)))
        );

        // MAGIC_TOLERANCE — COMMON(10), maxLevel 4, ARMOR, tag: enchantable/sanity_defend, 互斥 FLEXIBILITY
        register(context, MAGIC_TOLERANCE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                10, 4,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ARMOR
                        )
                )
                        .exclusiveWith(HolderSet.direct(enchGetter.getOrThrow(FLEXIBILITY)))
        );

        // HAZARD_PROTECTION — UNCOMMON(5), maxLevel 2, ARMOR, tag: enchantable/sanity_defend, 互斥 FLEXIBILITY, isTreasureOnly=true
        register(context, HAZARD_PROTECTION,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                5, 2,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ARMOR
                        )
                )
                        .exclusiveWith(HolderSet.direct(enchGetter.getOrThrow(FLEXIBILITY)))
        );

        // SANITY_INJURY_CURSE — COMMON(10), maxLevel 1, ANY, tag: enchantable/sanity_defend, 互斥 SANITY_DEFEND, isCurse=true, isTradeable=false
        register(context, SANITY_INJURY_CURSE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                10, 1,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ANY
                        )
                )
                        .exclusiveWith(HolderSet.direct(enchGetter.getOrThrow(SANITY_DEFEND)))
        );

        // REJECTION_CURSE — VERY_RARE(1), maxLevel 1, ARMOR, tag: enchantable/sanity_defend, isCurse=true, isTradeable=false
        register(context, REJECTION_CURSE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemGetter.getOrThrow(tag("enchantable/sanity_defend")),
                                1, 1,
                                Enchantment.dynamicCost(11, 10),
                                Enchantment.dynamicCost(16, 10),
                                2,
                                EquipmentSlotGroup.ARMOR
                        )
                )
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, name));
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, name));
    }

    /**
     * 运行时从注册表查找 Holder<Enchantment>，替代 1.20.1 的 DeferredHolder.get()
     */
    public static Holder<Enchantment> getHolder(HolderLookup.Provider provider, ResourceKey<Enchantment> key) {
        return provider.lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(key);
    }
}