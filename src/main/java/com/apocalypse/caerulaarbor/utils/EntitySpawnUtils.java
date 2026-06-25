package com.apocalypse.caerulaarbor.utils;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import java.util.List;
import java.util.Optional;

public class EntitySpawnUtils {

	private static final TagKey<EntityType<?>> ENTITY_TAG = TagKey.create(
			Registries.ENTITY_TYPE,
			new ResourceLocation(CaerulaArborMod.MODID, "izumik_discovers")
	);

	private EntitySpawnUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean spawnRandomEntityFromTag(ServerLevel serverLevel, double x, double y, double z){
		if(serverLevel == null) return false;
		return spawnEntity(serverLevel, x, y, z);
	}

	public static Optional<EntityType<?>> randomEntityTypeInTag(Level level, TagKey<EntityType<?>> tag) {
		Registry<EntityType<?>> registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
		Optional<HolderSet.Named<EntityType<?>>> optional = registry.getTag(tag);
		if (optional.isEmpty()) return Optional.empty();
		List<Holder<EntityType<?>>> entitiesInTag = optional.get().stream().toList();
		if (entitiesInTag.isEmpty()) return Optional.empty();
		RandomSource random = level.getRandom();
		EntityType<?> selected = entitiesInTag.get(random.nextInt(entitiesInTag.size())).value();
		return Optional.of(selected);
	}

	private static boolean spawnEntity(ServerLevel level, double x, double y,double z) {
		Optional<EntityType<?>> optionalEntityType = randomEntityTypeInTag(level, ENTITY_TAG);
		if (optionalEntityType.isEmpty()) return false;
		EntityType<?> entityType = optionalEntityType.get();
		if(entityType == null) return false;
		Entity entity = entityType.create(level);
		if (entity == null) return false;

		entity.moveTo(x,y,z,
				level.getRandom().nextFloat() * 360.0F,
				0.0F
		);

		level.addFreshEntity(entity);
		return true;
	}
}
