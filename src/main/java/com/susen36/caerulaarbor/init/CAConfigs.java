package com.susen36.caerulaarbor.init;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CAConfigs {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;

	public static final ModConfigSpec.ConfigValue<List<? extends String>> LIGHTS_FOOD;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> HAND_STRANGLE;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> HAND_FIREWORK;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> HAND_ENGRAVE;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> CRIMSON_TREATY;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> BOIL_WATER;
	public static final ModConfigSpec.ConfigValue<Double> COEFFICIENT;
	public static final ModConfigSpec.ConfigValue<Boolean> BREAKABLE;
	public static final ModConfigSpec.ConfigValue<Boolean> EVOSOUND;
	public static final ModConfigSpec.ConfigValue<Double> CLONE_NUM;
	public static final ModConfigSpec.ConfigValue<Double> SANITY_BREAK;
	public static final ModConfigSpec.ConfigValue<Double> OCEANIZE_HEALTH;
	public static final ModConfigSpec.ConfigValue<Double> HEALTH_MULT;
	public static final ModConfigSpec.ConfigValue<Double> ATTACK_MULT;
	public static final ModConfigSpec.ConfigValue<Double> ARMOR_MULT;
	public static final ModConfigSpec.ConfigValue<Double> MORTAR_HEALTH;
	public static final ModConfigSpec.ConfigValue<Double> MORTAR_ATTACK;
	public static final ModConfigSpec.ConfigValue<Boolean> SANITY_BAR_STYLE;
	public static final ModConfigSpec.ConfigValue<Boolean> LIGHTS_NEAT_STYLE;
	public static final ModConfigSpec.ConfigValue<Double> X_OFFSET;
	public static final ModConfigSpec.ConfigValue<Double> Y_OFFSET;
	public static final ModConfigSpec.ConfigValue<Double> X_OFFSET_LIGHT;
	public static final ModConfigSpec.ConfigValue<Double> Y_OFFSET_LIGHT;
	public static final ModConfigSpec.ConfigValue<Double> X_OFFSET_LIFE;
	public static final ModConfigSpec.ConfigValue<Double> Y_OFFSET_LIFE;
	public static final ModConfigSpec.ConfigValue<Boolean> BOSSBAR;
	public static final ModConfigSpec.ConfigValue<Boolean> EXTERNAL_ERROSION;
	public static final ModConfigSpec.ConfigValue<Double> LP_LIMIT;
	public static final ModConfigSpec.ConfigValue<Double> SHIELD_LIMIT;
	public static final ModConfigSpec.ConfigValue<Boolean> CROSSOVER;
	public static final ModConfigSpec.ConfigValue<Boolean> TRANS_BOSS;
	public static final ModConfigSpec.ConfigValue<Boolean> DEBUG;
	public static final ModConfigSpec.ConfigValue<Double> LP_INIT;
	public static final ModConfigSpec.ConfigValue<Boolean> SANITY_PTC;
	public static final ModConfigSpec.ConfigValue<Double> X_OFFSET_SHIELD;
	public static final ModConfigSpec.ConfigValue<Double> Y_OFFSET_SHIELD;
	public static final ModConfigSpec.ConfigValue<Double> X_OFFSET_ECHO;
	public static final ModConfigSpec.ConfigValue<Double> Y_OFFSET_ECHO;
	public static final ModConfigSpec.ConfigValue<Boolean> SUBLIMATION_BAN;
	public static final ModConfigSpec.ConfigValue<Double> SUBLIMATION_COEFFICIENT;
	static {
		BUILDER.push("foods");
		LIGHTS_FOOD = BUILDER.comment("可用于恢复灯火的食物及其恢复量，“注册名, 最小恢复量/最大恢复量”，逗号后有1空格。").defineList("lights_revovery",
				List.of("minecraft:glow_berries, 2/3", "minecraft:golden_carrot, 1/2", "minecraft:golden_apple, 2/3", "minecraft:enchanted_golden_apple, 3/5"), entry -> true);
		BUILDER.pop();
		BUILDER.push("relics");
		HAND_STRANGLE = BUILDER.comment("其它能使收藏品扼喉之手生效的物品。最好是弩。用 * 号代表该命名空间下的所有物品，如 tacz:*, caerula_arbor:oceanized_* 等，下同").defineList("hand_strangle_recognizable", List.of("CrossbowMod2:item.woodenCrossbow"), entry -> true);
		HAND_FIREWORK = BUILDER.comment("其它能使收藏品烟花之手生效的物品。最好是弓。").defineList("hand_firework_recognizable", List.of("twilightforest:triple_bow"), entry -> true);
		HAND_ENGRAVE = BUILDER.comment("已弃用").defineList("hand_engrave_recognizable", List.of("iceandfire:tide_trident"), entry -> true);
		CRIMSON_TREATY = BUILDER.comment("其它能使收藏品绯红盟约生效的生物。最好来自下界。").defineList("crimson_treaty_recognizable", List.of("cataclysm:ignis", "cataclysm:netherite_monstrosity"), entry -> true);
		BUILDER.pop();
		BUILDER.push("blocks");
		BOIL_WATER = BUILDER.comment("其它能加热热水壶的方块。最好带点火。").defineList("water_boil_block", List.of("create:blaze_burner", "cataclysm:altar_of_fire"), entry -> true);
		BUILDER.pop();
		BUILDER.push("gameplay");
		COEFFICIENT = BUILDER.comment("进化点数系数，实际进化所需点数=(下一阶段代数^3)*系数。！请使用浮点数，下同！").define("evolution_point_coefficient", (double) 200);
		BREAKABLE = BUILDER.comment("开启后，模组生物的行为将受游戏规则：生物破坏的影响。").define("enbale_break", true);
		EVOSOUND = BUILDER.comment("允许进化进入新阶段后播放音效提醒（不影响用命令调整进化时的音效）。").define("enable_evolution_sound", true);
		CLONE_NUM = BUILDER.comment("全局增殖上限。游戏规则增殖上限的最大有效值。").define("global_clone_number_limit", (double) 24);
		SANITY_BREAK = BUILDER.comment("损伤爆发基础伤害。对非玩家生物的伤害上限为该值的 6 倍。").define("sanity_break_damage_base", (double) 12);
		OCEANIZE_HEALTH = BUILDER.comment("海嗣化基础生命值界限。用于确定计划外海嗣化时转变为精英单位的生命值分界线。").define("oceanize_health_base", (double) 9);
		EXTERNAL_ERROSION = BUILDER.comment("海嗣能够腐蚀来自真菌感染：孢子和魔法病的有机方块。").define("external_errosion", true);
		LP_LIMIT = BUILDER.comment("全局目标生命上限。").define("life_point_global_limit", (double) 32767);
		SHIELD_LIMIT = BUILDER.comment("全局护盾值上限。").define("player_shield_global_limit", (double) 99999);
		CROSSOVER = BUILDER.comment("仅使用本模组物品召唤联动头目。").define("independent_crossover", false);
		TRANS_BOSS = BUILDER.comment("是否海嗣化Boss生物。").define("boss_oceanization", false);
		DEBUG = BUILDER.comment("在日志输出调试信息。").define("show_debug_info", false);
		LP_INIT = BUILDER.comment("初始目标生命。").define("initial_life_point", (double) 6);
		SUBLIMATION_BAN = BUILDER.comment("禁用升华策略。").define("ban_sublimation", false);
		SUBLIMATION_COEFFICIENT = BUILDER.comment("升华进化点数系数，实际进化所需点数=(下一阶段代数^3)*系数*12。").define("sublimation_coefficient", (double) 1);
		BUILDER.pop();
		BUILDER.push("attribute");
		HEALTH_MULT = BUILDER.comment("海嗣最大生命乘数。1为原值，小于 0.1 的值无效，不建议使用过大的值。").define("health_multiplier", (double) 1);
		ATTACK_MULT = BUILDER.comment("海嗣攻击伤害乘数。1为原值。").define("attack_multiplier", (double) 1);
		ARMOR_MULT = BUILDER.comment("海嗣盔甲乘数。1为原值。").define("armor_multiplier", (double) 1);
		MORTAR_HEALTH = BUILDER.comment("阿戈尔重炮速射炮的生命值。").define("mortar_health", (double) 500);
		MORTAR_ATTACK = BUILDER.comment("阿戈尔重炮速射炮的基础伤害。").define("mortar_attack", (double) 32);
		BUILDER.pop();
		BUILDER.push("overlay");
		SANITY_BAR_STYLE = BUILDER.comment("神经损伤显示采用条状，若关闭则为明日方舟中的圆环。").define("sanity_bar_style", false);
		LIGHTS_NEAT_STYLE = BUILDER.comment("灯火显示采用简约风格，true代表开启。").define("lights_neat_style", false);
		X_OFFSET = BUILDER.comment("神经损伤条渲染x轴偏移。正数值为向右偏移").define("sanity_x_offset", (double) 0);
		Y_OFFSET = BUILDER.comment("神经损伤条渲染y轴偏移。正数值为向下偏移").define("sanity_y_offset", (double) 0);
		X_OFFSET_LIGHT = BUILDER.comment("灯火渲染x轴偏移。正数值为向右偏移").define("lights_x_offset", (double) 0);
		Y_OFFSET_LIGHT = BUILDER.comment("灯火渲染y轴偏移。正数值为向下偏移").define("lights_y_offset", (double) 0);
		X_OFFSET_LIFE = BUILDER.comment("目标生命值渲染x轴偏移。正数值为向右偏移").define("life_point_x_offset", (double) 0);
		Y_OFFSET_LIFE = BUILDER.comment("目标生命值渲染y轴偏移。正数值为向下偏移").define("life_point_y_offset", (double) 0);
		BOSSBAR = BUILDER.comment("渲染自定义Boss血条。").define("custom_boss_bar", true);
		SANITY_PTC = BUILDER.comment("造成额外精神损伤时显示粒子效果。").define("sanity_particle", true);
		X_OFFSET_SHIELD = BUILDER.comment("护盾值渲染x轴偏移，注意为相对目标生命UI的偏移").define("shield_x_offset", (double) 0);
		Y_OFFSET_SHIELD = BUILDER.comment("护盾值渲染y轴偏移，注意为相对目标生命UI的偏移").define("shield_y_offset", (double) 0);
		X_OFFSET_ECHO = BUILDER.comment("大群的回响渲染x轴偏移。正数值为向右偏移").define("echo_x_offset", (double) 0);
		Y_OFFSET_ECHO = BUILDER.comment("大群的回响渲染y轴偏移。正数值为向下偏移").define("echo_y_offset", (double) 0);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}