package com.susen36.caerulaarbor.entity.base;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CADamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public interface RangedSanityAttacker {
	default void performRangedSanityAttack() {
		if (this instanceof Entity center) {
			Level level = center.level();
			double x = center.getX();
			double y = center.getY();
			double z = center.getZ();
			double attackDamage = center instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;

			if (center instanceof LivingEntity selfEntity) {
				float selfDamage = selfEntity.getMaxHealth() * 0.3f;
				selfEntity.setHealth(selfEntity.getHealth() - selfDamage);
			}

			if (level instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y + 1, z, 1, 0, 0, 0, 0.5);
				serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 1, 0, 0, 0, 0.5);
			}

			level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0F, 1.0F);

			Vec3 centerPos = new Vec3(x, y + 1, z);
			double radius = 3.0D;
			List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, new AABB(centerPos, centerPos).inflate(radius), entity -> entity != center).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(centerPos))).toList();
			for (Entity entity : nearbyEntities) {
				if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
					continue;
				}
				if (!(entity instanceof LivingEntity)) {
					continue;
				}
				entity.hurt(CADamageTypes.source(level, CADamageTypes.OCEAN_MAGIC, center),
						(float) attackDamage);
				if (center instanceof LivingEntity attacker && entity instanceof LivingEntity target) {
					SIHelper.causeSanityInjury(target, attacker, attackDamage * 150, SanityEvent.Hurt.Type.ENTITY);
				}
			}
		}
	}
}
