package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.relic.RelicTier;
import com.susen36.caerulaarbor.relic.RelicType;
import com.susen36.caerulaarbor.relic.RelicType.BooleanRelicType;
import com.susen36.caerulaarbor.relic.RelicType.NumericRelicType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * 收藏品注册中心
 * <ol>
 *   <li>ResourceKey.createRegistryKey(caerula_arbor:relics) 创建自定义 RegistryKey</li>
 *   <li>RegistryBuilder + NewRegistryEvent 向 NeoForge 根注册表注册我们的自定义 Registry</li>
 *   <li>DeferredRegister&lt;RelicType&gt; 统一注册所有遗物条目，第三方 Mod 可通过同一个 DeferredRegister 注入新遗物</li>
 * </ol>
 */
public final class CARelics {

    public static final ResourceKey<Registry<RelicType>> RELICS_REGISTRY_KEY = RelicType.createRegistryKey(CaerulaArbor.MODID, "relics");
    public static final Registry<RelicType> RELICS_REGISTRY = new RegistryBuilder<>(RELICS_REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<RelicType> REGISTRY = DeferredRegister.create(RELICS_REGISTRY, CaerulaArbor.MODID);

    /* ========== featured / util 食物类 ========== */
    public static final DeferredHolder<RelicType, RelicType> FEATURED_CANNED_MEAT = REGISTRY.register("featured_canned_meat", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> SEAWEED_SALAD = REGISTRY.register("seaweed_salad", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> ORANGE_STORM = REGISTRY.register("orange_storm", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> COFFEE_PLAINS_COFFEE_CANDY = REGISTRY.register("coffee_plains_coffee_candy", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> PITTS_ASSORTED_FRUITS = REGISTRY.register("pitts_assorted_fruits", () -> new BooleanRelicType());

    /* ========== cursed 诅咒类 ========== */
    public static final DeferredHolder<RelicType, RelicType> CURSED_EMELIGHT = REGISTRY.register("cursed_emelight", () -> new BooleanRelicType(RelicTier.CURSED));
    public static final DeferredHolder<RelicType, RelicType> CURSED_GLOWBODY = REGISTRY.register("cursed_glowbody", () -> new BooleanRelicType(RelicTier.CURSED));
    public static final DeferredHolder<RelicType, RelicType> CURSED_RESEARCH = REGISTRY.register("cursed_research", () -> new BooleanRelicType(RelicTier.CURSED));
    public static final DeferredHolder<RelicType, RelicType> CURSED_HEART = REGISTRY.register("cursed_heart", () -> new BooleanRelicType(RelicTier.CURSED));

    /* ========== king 国王系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> KING_CROWN = REGISTRY.register("king_crown", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> KING_ARMOR = REGISTRY.register("king_armor", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> KING_SPEAR = REGISTRY.register("king_spear", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> KING_EXTENSION = REGISTRY.register("king_extension", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> KING_CRYSTAL = REGISTRY.register("king_crystal", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> ROYALFATE = REGISTRY.register("royalfate", () -> new BooleanRelicType(RelicTier.RARE));

    /* ========== hand 职业手系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> HAND_THORNS = REGISTRY.register("hand_thorns", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_STRANGLE = REGISTRY.register("hand_strangle", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_FERTILITY = REGISTRY.register("hand_fertility", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_SPEED = REGISTRY.register("hand_speed", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_OF_PULVERIZATION = REGISTRY.register("hand_of_pulverization", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_SWIPE = REGISTRY.register("hand_swipe", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_FIREWORK = REGISTRY.register("hand_firework", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_ENGRAVE = REGISTRY.register("hand_engrave", () -> new NumericRelicType(-1, 99, -1, RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> HAND_SWORD = REGISTRY.register("hand_sword", () -> new BooleanRelicType(RelicTier.ADVANCED));

    /* ========== archfi 始源之骸 ========== */
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_ARTIFACT = REGISTRY.register("sarkaz_king_artifact", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_FLAG = REGISTRY.register("sarkaz_king_flag", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_BED = REGISTRY.register("sarkaz_king_bed", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_RYLFATE = REGISTRY.register("sarkaz_king_rylfate", () -> new BooleanRelicType(RelicTier.ADVANCED));

    /* ========== special / misc ========== */
    public static final DeferredHolder<RelicType, RelicType> SURVIVOR_CONTRACT = REGISTRY.register("survivor_contract", () -> new NumericRelicType(-1, 32, -1, RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> TREATY = REGISTRY.register("treaty", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> NURTURE_GENE_SET = REGISTRY.register("nurture_gene_set", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> OIL_AND_CREAM = REGISTRY.register("oil_and_cream", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> TULIP_MEDCINE = REGISTRY.register("tulip_medcine", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> GOLDEN_CHALISE = REGISTRY.register("golden_chalise", () -> new BooleanRelicType(RelicTier.ADVANCED));
    public static final DeferredHolder<RelicType, RelicType> LEGEND_CHITIN = REGISTRY.register("legend_chitin", () -> new BooleanRelicType(RelicTier.ADVANCED));

    /* ========== util 实用物系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> UTIL_MUSICBOX = REGISTRY.register("util_musicbox", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_IRIS = REGISTRY.register("util_iris", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> WEIRD_FLUTE = REGISTRY.register("weird_flute", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> PURE_GOLD_EXPEDITION = REGISTRY.register("pure_gold_expedition", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> DURIN_OVERGROUND_ODYSSEY = REGISTRY.register("durin_overground_odyssey", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_TOPONYM = REGISTRY.register("util_toponym", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> HOT_WATER_KETTLE = REGISTRY.register("hot_water_kettle", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_ALLEY = REGISTRY.register("util_alley", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> VAMPIRES_BED = REGISTRY.register("vampires_bed", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> PROOF_OF_LONGEVITY = REGISTRY.register("proof_of_longevity", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_OMNIKEY = REGISTRY.register("util_omnikey", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_SCORE = REGISTRY.register("util_score", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_RESCISSION = REGISTRY.register("util_rescission", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_STARE = REGISTRY.register("util_stare", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_ALLAY = REGISTRY.register("util_allay", () -> new BooleanRelicType());
    public static final DeferredHolder<RelicType, RelicType> UTIL_RAINBOW = REGISTRY.register("util_rainbow", () -> new BooleanRelicType());

    /* ========== diso 不适体系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> DISO = REGISTRY.register("diso", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> DISO_FLESH = REGISTRY.register("diso_flesh", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> DISO_BLOOD = REGISTRY.register("diso_blood", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> DISO_NEURO = REGISTRY.register("diso_neuro", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> DISO_ATTENTION = REGISTRY.register("diso_attention", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> AHND_SWIPE = REGISTRY.register("ahnd_swipe", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> HANSHAND_SPIKE = REGISTRY.register("hanshand_spike", () -> new BooleanRelicType(RelicTier.RARE));

    /* ========== relic 杂项 ========== */
    public static final DeferredHolder<RelicType, RelicType> HEMOST = REGISTRY.register("hemost", () -> new BooleanRelicType(RelicTier.RARE));
    public static final DeferredHolder<RelicType, RelicType> YEARNING = REGISTRY.register("yearning", () -> new BooleanRelicType(RelicTier.ADVANCED));

    private CARelics() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 注册 DeferredRegister 条目。
     * 需要在 CaerulaArbor 构造器里和 ModCapabilities.register() 类似位置调用。
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CARelics::onRelicRegister);
    }

    private static void onRelicRegister(NewRegistryEvent event) {
        event.register(RELICS_REGISTRY);
    }
}
