package com.susen36.caerulaarbor.manager.spwan;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.TribunalHealerEntity;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAGameRules;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

public class SeabornTransformManager {
	private static final TagKey<EntityType<?>> HOMO_SAPIENS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "homo_sapiens"));

	private static final List<TransformRule> STANDARD_TRANSFORM_RULES = List.of(
			new TransformRule(entity -> entity instanceof Villager livingEntity && !livingEntity.isBaby() || matchesEntityType(entity, "guardvillagers:guard"), 0.375,
					CAEntities.OCEANIZED_VILLAGER.get()),
			new TransformRule(entity -> entity instanceof Shulker, 0.25, CAEntities.OCEANIZED_SHULKER.get()),
			new TransformRule(entity -> entity instanceof Chicken, 0.45, CAEntities.OCEANIZED_CHICKEN.get()),
			new TransformRule(entity -> entity instanceof TribunalHealerEntity, 0.15, CAEntities.COMPASSION_PRAYER.get()),
			new TransformRule(entity -> entity instanceof Rabbit, 0.45, CAEntities.OCEANIZE_RABBIT.get()),
			new TransformRule(entity -> entity instanceof PolarBear, 0.35, CAEntities.OCEANIZED_POLAR_BEAR.get()),
			new TransformRule(entity -> matchesEntityType(entity, "bobsoriginiumdream:mutant_giant_rock_spider"), 0.32, CAEntities.TIDUTANT_ROCK_SPIDER.get()),
			new TransformRule(entity -> matchesEntityType(entity, "bobsoriginiumdream:originiutant_excrescence"), 0.5, CAEntities.TIDUTANT_EXCRESCENCE.get()),
			new TransformRule(entity -> entity instanceof Fox, 0.5, CAEntities.OCEANIZED_FOX.get()),
			new TransformRule(entity -> entity.getType().is(HOMO_SAPIENS), 0.25, CAEntities.THE_ABANDONED.get()),
			new TransformRule(entity -> entity instanceof Evoker, 0.25, CAEntities.OCEANIZED_EVOKER.get()),
			new TransformRule(entity -> entity instanceof Vindicator, 0.3, CAEntities.OCEANIZED_VINDICATOR.get()),
			new TransformRule(entity -> entity instanceof Pillager, 0.3, CAEntities.OCEANIZED_PILLAGER.get()),
			new TransformRule(entity -> entity instanceof Pig, 0.5, CAEntities.OCEANIZED_PIG.get()),
			new TransformRule(entity -> entity instanceof Cow || entity instanceof MushroomCow, 0.45, CAEntities.OCEANIZED_COW.get()),
			new TransformRule(entity -> entity instanceof Sheep, 0.45, CAEntities.OCEANIZED_SHEEP.get()),
			new TransformRule(entity -> entity instanceof Horse, 0.35, CAEntities.OCEANIZED_HORSE.get()),
			new TransformRule(entity -> entity instanceof Piglin, 0.4, CAEntities.OCEANIZED_PIGLIN.get()),
			new TransformRule(entity -> entity instanceof PiglinBrute, 0.2, CAEntities.OCEANIZED_BRUTE.get()),
			new TransformRule(entity -> entity instanceof Spider || entity instanceof CaveSpider || matchesEntityType(entity, "twilightforest:hedge_spider") || matchesEntityType(entity, "twilightforest:king_spider"), 0.65,
					CAEntities.OCEANIZED_SPIDER.get()),
			new TransformRule(entity -> entity instanceof EnderMan, 0.2, CAEntities.OCEANIZED_ENDERMAN.get()),
			new TransformRule(entity -> entity instanceof Wolf wolf && !wolf.isTame(), 0.2, CAEntities.OCEANIZED_WOLF.get()),
			new TransformRule(entity -> entity instanceof Wolf wolf && wolf.isTame(), 1.0, CAEntities.OCEANIZED_DOG.get()),
			new TransformRule(entity -> entity instanceof Witch, 0.33, CAEntities.OCEANIZED_WITCH.get()),
			new TransformRule(entity -> entity instanceof Ravager, 0.25, CAEntities.OCEANIZED_RAVAGER.get()),
			new TransformRule(entity -> entity instanceof Warden, 0.1, (world, x, y, z, entity) -> spawnReplacement(world, x, y, z, entity, current -> current instanceof Warden,
					Math.random() < 0.02 ? CAEntities.OCEANIZED_WARDENIS.get() : CAEntities.OCEANIZED_WARDEN.get())),
			new TransformRule(entity -> entity instanceof Cat || entity instanceof Ocelot, 0.5, CAEntities.OCEANIZED_CAT.get()));

	public static boolean transformToSeaborn(Level world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		boolean trans = false;
		double rate;
		double h;
		if (entity instanceof Player ||getEntityTypeId(entity).contains("touhou_little_maid:maid")) {
			return false;
		}
		if (!(entity instanceof LivingEntity livEnt2 && livEnt2.getType().is(EntityTypeTags.UNDEAD) || entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "cannot_transform"))))
				&& world.getLevelData().getGameRules().getBoolean(CAGameRules.OCEANIZATION_MODE) && !(entity instanceof LivingEntity livEnt5 && livEnt5.isBaby())) {
			if (EntityUtils.getSeabornAround(world, x, y, z, entity) > Math.min((world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT)), CAConfigs.CLONE_NUM.get()) * 2) {
				return false;
			}
			TransformAttemptResult standardTransformResult = tryStandardTransforms(world, x, y, z, entity);
			if (standardTransformResult != TransformAttemptResult.NO_MATCH) {
				trans = standardTransformResult == TransformAttemptResult.SUCCESS;
			} else if (Math.random() < 0.25) {
				rate = 0.15;
				h = CAConfigs.OCEANIZE_HEALTH.get();
				if ((entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) < h) {
					rate = 0;
				}
				if ((entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) > h * 4) {
					rate = 0.75;
				}
				if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:bosses")))) {
					rate = 1;
				}
				SeabornSpawnManager.summonRandomSeaborn(world, rate, x, y, z);
				trans = true;
			}
			if (trans) {
				if (world instanceof Level level) {
					if (!level.isClientSide()) {
						level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 1, 1);
					} else {
						level.playLocalSound(x, y, z, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 1, 1, false);
					}
				}
				if (world instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 0.2), z, 2, 0.1, 0.1, 0.1, 0.15);
			}
		}
		return trans;
	}

	private static TransformAttemptResult tryStandardTransforms(Level world, double x, double y, double z, Entity entity) {
		for (TransformRule rule : STANDARD_TRANSFORM_RULES) {
			if (rule.condition().test(entity)) {
				if (Math.random() >= rule.chance()) {
					return TransformAttemptResult.FAILED;
				}
				if (rule.executor().apply(world, x, y, z, entity)) {
					return TransformAttemptResult.SUCCESS;
				}
				return TransformAttemptResult.FAILED;
			}
		}
		return TransformAttemptResult.NO_MATCH;
	}

	private static boolean spawnReplacement(Level world, double x, double y, double z, Entity originalEntity, Predicate<Entity> condition, EntityType<? extends Mob> replacementType) {
		if (!condition.test(originalEntity)) {
			return false;
		}
		if (originalEntity instanceof Mob mob && world instanceof ServerLevel) {
			Mob converted = mob.convertTo(replacementType, false);
			if (converted != null) {
				converted.setYRot(originalEntity.getYRot());
				converted.setYBodyRot(originalEntity.getYRot());
				converted.setYHeadRot(originalEntity.getYRot());
				converted.setXRot(originalEntity.getXRot());
			}
		}
		return true;
	}

	private static String getEntityTypeId(Entity entity) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
	}

	private static boolean matchesEntityType(Entity entity, String entityTypeId) {
		return getEntityTypeId(entity).equals(entityTypeId);
	}

	private record TransformRule(Predicate<Entity> condition, double chance, TransformExecutor executor) {
		private TransformRule(Predicate<Entity> condition, double chance, EntityType<? extends Mob> replacementType) {
			this(condition, chance, (world, x, y, z, entity) -> spawnReplacement(world, x, y, z, entity, current -> true, replacementType));
		}
	}

	private enum TransformAttemptResult {
		NO_MATCH,
		FAILED,
		SUCCESS
	}

	@FunctionalInterface
	private interface TransformExecutor {
		boolean apply(Level world, double x, double y, double z, Entity entity);
	}
}