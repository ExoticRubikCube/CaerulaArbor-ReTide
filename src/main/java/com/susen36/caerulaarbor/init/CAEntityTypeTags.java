package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * 自定义实体类型标签常量。
 *
 * <p>这个接口只负责提供 {@link TagKey}，用于
 * {@code entity.getType().is(CAEntityTypeTags.XXX)} 这类判断，
 * 表达"已有实体属于哪一类"。它不负责生成标签数据；
 * 生成逻辑统一放在 {@code datagen.tags.EntityTypeTagsProvider}。
 */
public interface CAEntityTypeTags {
	TagKey<EntityType<?>> HUMAN = create("is_humanside");
	TagKey<EntityType<?>> SEABORN = create("seaborn");
	TagKey<EntityType<?>> SEABORN_BOSS = create("seaborn_boss");
	TagKey<EntityType<?>> SEABORN_MINION = create("seaborn_minion");
	TagKey<EntityType<?>> SEABORN_PET = create("seaborn_pet");
	TagKey<EntityType<?>> TINY_SEABORN = create("tiny_seaborn");
	TagKey<EntityType<?>> SEA_FRIEND = create("sea_friend");
	TagKey<EntityType<?>> ELITE = create("elite");
	TagKey<EntityType<?>> OCEAN_ELITE = create("oceanelite");
	TagKey<EntityType<?>> BOSSES = create("bosses");

	private static TagKey<EntityType<?>> create(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, name));
	}
}
