package com.apocalypse.caerulaarbor.init;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CAConfigs {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LIGHTS_FOOD;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HAND_STRANGLE;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HAND_FIREWORK;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HAND_ENGRAVE;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CRIMSON_TREATY;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BOIL_WATER;
	public static final ForgeConfigSpec.ConfigValue<Double> COEFFICIENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BREAKABLE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> EVOSOUND;
	public static final ForgeConfigSpec.ConfigValue<Double> CLONE_NUM;
	public static final ForgeConfigSpec.ConfigValue<Double> SANITY_BREAK;
	public static final ForgeConfigSpec.ConfigValue<Double> OCEANIZE_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> RELIC_BAN;
	public static final ForgeConfigSpec.ConfigValue<Double> HEALTH_MULT;
	public static final ForgeConfigSpec.ConfigValue<Double> ATTACK_MULT;
	public static final ForgeConfigSpec.ConfigValue<Double> ARMOR_MULT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SANITY_BAR_STYLE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LIGHTS_NEAT_STYLE;
	public static final ForgeConfigSpec.ConfigValue<Double> X_OFFSET;
	public static final ForgeConfigSpec.ConfigValue<Double> Y_OFFSET;
	public static final ForgeConfigSpec.ConfigValue<Double> X_OFFSET_LIGHT;
	public static final ForgeConfigSpec.ConfigValue<Double> Y_OFFSET_LIGHT;
	public static final ForgeConfigSpec.ConfigValue<Double> X_OFFSET_LIFE;
	public static final ForgeConfigSpec.ConfigValue<Double> Y_OFFSET_LIFE;
	public static final ForgeConfigSpec.ConfigValue<Double> X_OFFSET_ATTR;
	public static final ForgeConfigSpec.ConfigValue<Double> Y_OFFSET_ATTR;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BOSSBAR;
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
		RELIC_BAN = BUILDER.comment("禁用高级收藏品。无法持有且无法激活。").define("ban_advanced_relics", false);
		BUILDER.pop();
		BUILDER.push("attribute");
		HEALTH_MULT = BUILDER.comment("海嗣最大生命乘数。1为原值，小于 0.1 的值无效，不建议使用过大的值。").define("health_multiplier", (double) 1);
		ATTACK_MULT = BUILDER.comment("海嗣攻击伤害乘数。1为原值。").define("attack_multiplier", (double) 1);
		ARMOR_MULT = BUILDER.comment("海嗣盔甲乘数。1为原值。").define("armor_multiplier", (double) 1);
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
		X_OFFSET_ATTR = BUILDER.comment("模组属性渲染x轴偏移。正数值为向右偏移").define("attr_x_offset", (double) 0);
		Y_OFFSET_ATTR = BUILDER.comment("模组属性渲染y轴偏移。正数值为向下偏移").define("attr_y_offset", (double) 0);
		BOSSBAR = BUILDER.comment("渲染自定义Boss血条。").define("custom_boss_bar", true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
