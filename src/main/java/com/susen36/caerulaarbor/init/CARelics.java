package com.susen36.caerulaarbor.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 遗物兼容别名层：将旧 CARelics 遗物标记映射到 {@link CAItems} 中对应的收集品/物品字段。
 * <p>
 * 旧系统基于自定义 caerula_arbor:relics 注册表（CollectibleLike 标记），现统一迁移到
 * babel Collectibles（以实际物品为键的 isUsed / getLayer / setLayer）。本类仅保留原字段名
 * 作为别名常量，供尚未迁移的调用点平滑过渡；逻辑一律走 {@link CAItems} + babel 附件 API。
 */
public final class CARelics {
	private CARelics() {
		throw new UnsupportedOperationException("Utility class");
	}

	/* ========== featured / util 食物类 ========== */
	public static final DeferredHolder<Item, ? extends Item> FEATURED_CANNED_MEAT = CAItems.FEATURED_CANNED_MEAT;
	public static final DeferredHolder<Item, ? extends Item> SEAWEED_SALAD = CAItems.BOWL_SEAGRASS;
	public static final DeferredHolder<Item, ? extends Item> ORANGE_STORM = CAItems.GOLDEN_STORM;
	public static final DeferredHolder<Item, ? extends Item> COFFEE_PLAINS_COFFEE_CANDY = CAItems.COFFEE_CANDY;
	public static final DeferredHolder<Item, ? extends Item> PITTS_ASSORTED_FRUITS = CAItems.CANNED_CHERRY;

	/* ========== cursed 诅咒类 ========== */
	public static final DeferredHolder<Item, ? extends Item> CURSED_EMELIGHT = CAItems.CURSED_EMELIGHT;
	public static final DeferredHolder<Item, ? extends Item> CURSED_GLOWBODY = CAItems.CURSED_GLOWBODY;
	public static final DeferredHolder<Item, ? extends Item> CURSED_RESEARCH = CAItems.CURSED_RESEARCH;
	public static final DeferredHolder<Item, ? extends Item> CURSED_HEART = CAItems.CURSED_HEART;

	/* ========== king 国王系列 ========== */
	public static final DeferredHolder<Item, ? extends Item> KING_CROWN = CAItems.KING_CROWN;
	public static final DeferredHolder<Item, ? extends Item> KING_ARMOR = CAItems.KING_ARMOR;
	public static final DeferredHolder<Item, ? extends Item> KING_SPEAR = CAItems.KING_SPEAR;
	public static final DeferredHolder<Item, ? extends Item> KING_EXTENSION = CAItems.KING_EXTENSION;
	public static final DeferredHolder<Item, ? extends Item> KING_CRYSTAL = CAItems.KING_CRYSTAL;
	public static final DeferredHolder<Item, ? extends Item> ROYALFATE = CAItems.ROYAL_FATE;

	/* ========== hand 职业手系列 ========== */
	public static final DeferredHolder<Item, ? extends Item> HAND_THORNS = CAItems.HAND_THORNS;
	public static final DeferredHolder<Item, ? extends Item> HAND_STRANGLE = CAItems.HAND_STRANGLE;
	public static final DeferredHolder<Item, ? extends Item> HAND_FERTILITY = CAItems.HAND_FERTILITY;
	public static final DeferredHolder<Item, ? extends Item> HAND_SPEED = CAItems.HAND_SPEED;
	public static final DeferredHolder<Item, ? extends Item> HAND_OF_PULVERIZATION = CAItems.HAND_OF_PULVERIZATION;
	public static final DeferredHolder<Item, ? extends Item> HAND_SWIPE = CAItems.HAND_SWIPE;
	public static final DeferredHolder<Item, ? extends Item> HAND_FIREWORK = CAItems.HAND_FIREWORK;
	public static final DeferredHolder<Item, ? extends Item> HAND_ENGRAVE = CAItems.HAND_OF_ENGRAVE;
	public static final DeferredHolder<Item, ? extends Item> HAND_SWORD = CAItems.HAND_SWORD;

	/* ========== archfi 始源之骸 ========== */
	public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_ARTIFACT = CAItems.SARKAZ_KING_ARTIFACT;
	public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_FLAG = CAItems.SARKAZ_KING_FLAG;
	public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_BED = CAItems.SARKAZ_KING_BED;
	public static final DeferredHolder<Item, ? extends Item> SARKAZ_KING_RYLFATE = CAItems.SARKAZ_KING_RYLFATE;

	/* ========== special / misc ========== */
	public static final DeferredHolder<Item, ? extends Item> SURVIVOR_CONTRACT = CAItems.SURVIVOR_CONTRACT;
	public static final DeferredHolder<Item, ? extends Item> TREATY = CAItems.TREATY;
	public static final DeferredHolder<Item, ? extends Item> NURTURE_GENE_SET = CAItems.NURTURE_GENE_SET;
	public static final DeferredHolder<Item, ? extends Item> OIL_AND_CREAM = CAItems.OIL_AND_CREAM;
	public static final DeferredHolder<Item, ? extends Item> TULIP_MEDCINE = CAItems.TULIP_MEDCINE;
	public static final DeferredHolder<Item, ? extends Item> GOLDEN_CHALISE = CAItems.GOLDEN_CHALISE;
	public static final DeferredHolder<Item, ? extends Item> LEGEND_CHITIN = CAItems.LEGEND_CHITIN;

	/* ========== util 实用物系列 ========== */
	public static final DeferredHolder<Item, ? extends Item> UTIL_MUSICBOX = CAItems.SOLO_MUSIC_BOX;
	public static final DeferredHolder<Item, ? extends Item> UTIL_IRIS = CAItems.REDSTONE_IRIS_FLOWER;
	public static final DeferredHolder<Item, ? extends Item> WEIRD_FLUTE = CAItems.ODD_FLUTE;
	public static final DeferredHolder<Item, ? extends Item> PURE_GOLD_EXPEDITION = CAItems.VOYAGE_OF_GOLD;
	public static final DeferredHolder<Item, ? extends Item> DURIN_OVERGROUND_ODYSSEY = CAItems.DURIN_OVERGROUND_ODYSSEY;
	public static final DeferredHolder<Item, ? extends Item> UTIL_TOPONYM = CAItems.TOPONYM_TEXTOLOGY;
	public static final DeferredHolder<Item, ? extends Item> HOT_WATER_KETTLE = CAItems.KETTLE;
	public static final DeferredHolder<Item, ? extends Item> UTIL_ALLEY = CAItems.UTIL_ALLAY;
	public static final DeferredHolder<Item, ? extends Item> VAMPIRES_BED = CAItems.VAMPIRES_BED;
	public static final DeferredHolder<Item, ? extends Item> PROOF_OF_LONGEVITY = CAItems.PROOF_OF_LONGEVITY;
	public static final DeferredHolder<Item, ? extends Item> UTIL_OMNIKEY = CAItems.UTIL_OMNIKEY;
	public static final DeferredHolder<Item, ? extends Item> UTIL_SCORE = CAItems.UTIL_SCORE;
	public static final DeferredHolder<Item, ? extends Item> UTIL_RESCISSION = CAItems.UTIL_RESCISSION;
	public static final DeferredHolder<Item, ? extends Item> UTIL_STARE = CAItems.UTIL_STARE;
	public static final DeferredHolder<Item, ? extends Item> UTIL_ALLAY = CAItems.UTIL_ALLAY;
	public static final DeferredHolder<Item, ? extends Item> UTIL_RAINBOW = CAItems.RAINBOW_CANDY;

	/* ========== diso 不适体系列 ========== */
	public static final DeferredHolder<Item, ? extends Item> DISO = CAItems.DISO;
	public static final DeferredHolder<Item, ? extends Item> DISO_FLESH = CAItems.DISO_FLESH;
	public static final DeferredHolder<Item, ? extends Item> DISO_BLOOD = CAItems.DISO_BLOOD;
	public static final DeferredHolder<Item, ? extends Item> DISO_NEURO = CAItems.DISO_NEURO;
	public static final DeferredHolder<Item, ? extends Item> DISO_ATTENTION = CAItems.DISO_ATTENTION;
	public static final DeferredHolder<Item, ? extends Item> AHND_SWIPE = CAItems.AHND_SWIPE;
	public static final DeferredHolder<Item, ? extends Item> HANSHAND_SPIKE = CAItems.HANSHAND_SPIKE;

	/* ========== relic 杂项 ========== */
	public static final DeferredHolder<Item, ? extends Item> HEMOST = CAItems.HEMOST;
	public static final DeferredHolder<Item, ? extends Item> YEARNING = CAItems.YEARNING;
}
