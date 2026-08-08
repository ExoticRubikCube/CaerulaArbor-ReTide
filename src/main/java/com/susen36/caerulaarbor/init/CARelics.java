package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArbor;
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
 * 遗物注册中心（方案 B：自定义 Registry + DeferredRegister）。
 * <p>
 * 与 CAItems / ModCapabilities 风格完全一致：
 * <ol>
 *   <li>ResourceKey.createRegistryKey(caerula_arbor:relics) 创建自定义 RegistryKey</li>
 *   <li>RegistryBuilder + NewRegistryEvent 向 NeoForge 根注册表注册我们的自定义 Registry</li>
 *   <li>DeferredRegister&lt;RelicType&gt; 统一注册所有遗物条目，第三方 Addon 可通过同一个 DeferredRegister 注入新遗物</li>
 * </ol>
 */
public final class CARelics {

    public static final ResourceKey<Registry<RelicType>> RELICS_REGISTRY_KEY = RelicType.createRegistryKey(CaerulaArbor.MODID, "relics");
    public static final Registry<RelicType> RELICS_REGISTRY = new RegistryBuilder<>(RELICS_REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<RelicType> REGISTRY = DeferredRegister.create(RELICS_REGISTRY, CaerulaArbor.MODID);

    /* ========== featured / util 食物类 ========== */
    public static final DeferredHolder<RelicType, RelicType> FEATURED_CANNED_MEAT = REGISTRY.register("featured_canned_meat", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> SEAWEED_SALAD = REGISTRY.register("seaweed_salad", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> ORANGE_STORM = REGISTRY.register("orange_storm", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> COFFEE_PLAINS_COFFEE_CANDY = REGISTRY.register("coffee_plains_coffee_candy", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> PITTS_ASSORTED_FRUITS = REGISTRY.register("pitts_assorted_fruits", BooleanRelicType::new);

    /* ========== cursed 诅咒类 ========== */
    public static final DeferredHolder<RelicType, RelicType> CURSED_EMELIGHT = REGISTRY.register("cursed_emelight", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> CURSED_GLOWBODY = REGISTRY.register("cursed_glowbody", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> CURSED_RESEARCH = REGISTRY.register("cursed_research", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> CURSED_HEART = REGISTRY.register("cursed_heart", BooleanRelicType::new);

    /* ========== king 国王系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> KING_CROWN = REGISTRY.register("king_crown", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> KING_ARMOR = REGISTRY.register("king_armor", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> KING_SPEAR = REGISTRY.register("king_spear", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> KING_EXTENSION = REGISTRY.register("king_extension", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> KING_CRYSTAL = REGISTRY.register("king_crystal", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> ROYALFATE = REGISTRY.register("royalfate", BooleanRelicType::new);

    /* ========== hand 职业手系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> HAND_THORNS = REGISTRY.register("hand_thorns", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_STRANGLE = REGISTRY.register("hand_strangle", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_FERTILITY = REGISTRY.register("hand_fertility", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_SPEED = REGISTRY.register("hand_speed", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_OF_PULVERIZATION = REGISTRY.register("hand_of_pulverization", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_SWIPE = REGISTRY.register("hand_swipe", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_FIREWORK = REGISTRY.register("hand_firework", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HAND_ENGRAVE = REGISTRY.register("hand_engrave", () -> new NumericRelicType(-1, 99, -1));
    public static final DeferredHolder<RelicType, RelicType> HAND_SWORD = REGISTRY.register("hand_sword", BooleanRelicType::new);

    /* ========== archfi 始源之骸 ========== */
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_ARTIFACT = REGISTRY.register("sarkaz_king_artifact", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_FLAG = REGISTRY.register("sarkaz_king_flag", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_BED = REGISTRY.register("sarkaz_king_bed", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> SARKAZ_KING_RYLFATE = REGISTRY.register("sarkaz_king_rylfate", BooleanRelicType::new);

    /* ========== special / misc ========== */
    public static final DeferredHolder<RelicType, RelicType> SURVIVOR_CONTRACT = REGISTRY.register("survivor_contract", () -> new NumericRelicType(-1, 32, -1));
    public static final DeferredHolder<RelicType, RelicType> TREATY = REGISTRY.register("treaty", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> LEGEND_CHITIN = REGISTRY.register("legend_chitin", BooleanRelicType::new);

    /* ========== util 实用物系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> UTIL_MUSICBOX = REGISTRY.register("util_musicbox", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_IRIS = REGISTRY.register("util_iris", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> WEIRD_FLUTE = REGISTRY.register("weird_flute", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> PURE_GOLD_EXPEDITION = REGISTRY.register("pure_gold_expedition", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> DURIN_OVERGROUND_ODYSSEY = REGISTRY.register("durin_overground_odyssey", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_TOPONYM = REGISTRY.register("util_toponym", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HOT_WATER_KETTLE = REGISTRY.register("hot_water_kettle", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_ALLEY = REGISTRY.register("util_alley", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> VAMPIRES_BED = REGISTRY.register("vampires_bed", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> PROOF_OF_LONGEVITY = REGISTRY.register("proof_of_longevity", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_OMNIKEY = REGISTRY.register("util_omnikey", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_SCORE = REGISTRY.register("util_score", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_RESCISSION = REGISTRY.register("util_rescission", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_STARE = REGISTRY.register("util_stare", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_ALLAY = REGISTRY.register("util_allay", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> UTIL_RAINBOW = REGISTRY.register("util_rainbow", BooleanRelicType::new);

    /* ========== diso 不适体系列 ========== */
    public static final DeferredHolder<RelicType, RelicType> DISO = REGISTRY.register("diso", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> DISO_FLESH = REGISTRY.register("diso_flesh", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> DISO_BLOOD = REGISTRY.register("diso_blood", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> DISO_NEURO = REGISTRY.register("diso_neuro", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> DISO_ATTENTION = REGISTRY.register("diso_attention", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> AHND_SWIPE = REGISTRY.register("ahnd_swipe", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> HANSHAND_SPIKE = REGISTRY.register("hanshand_spike", BooleanRelicType::new);

    /* ========== relic 杂项 ========== */
    public static final DeferredHolder<RelicType, RelicType> HEMOST = REGISTRY.register("hemost", BooleanRelicType::new);
    public static final DeferredHolder<RelicType, RelicType> YEARNING = REGISTRY.register("yearning", BooleanRelicType::new);

    private CARelics() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 在 mod event bus 上既注册自定义 Registry 本体（NewRegistryEvent），又注册 DeferredRegister 条目。
     * 需要在 CaerulaArbor 构造器里和 ModCapabilities.register() 类似位置调用。
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CARelics::onNewRegistry);
        REGISTRY.register(modEventBus);
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(RELICS_REGISTRY);
    }
}
