package com.apocalypse.caerulaarbor.entity.base;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

			new Object() {
				void timedLoop(int timedLoopIterator, int timedLoopTotal, int ticks) {
					if (level instanceof ServerLevel serverLevel) {
						serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1.5, z, 128, 3.5, 2, 3.5, 0.1);
					}
					CaerulaArborMod.queueServerWork(ticks, () -> {
						if (timedLoopTotal > timedLoopIterator + 1) {
							timedLoop(timedLoopIterator + 1, timedLoopTotal, ticks);
						}
					});
				}
			}.timedLoop(0, 5, 2);

			level.playSound(null, BlockPos.containing(x, y, z), CASounds.CREEPER_FISH_EXPLODE.get(), SoundSource.HOSTILE, 3, 1);

			Vec3 centerPos = new Vec3(x, y + 1.5, z);
			List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, new AABB(centerPos, centerPos).inflate(8 / 2d), entity -> true).stream().sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(centerPos))).toList();
			for (Entity entity : nearbyEntities) {
				if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
					continue;
				}
				if (!(entity instanceof LivingEntity)) {
					continue;
				}
				entity.hurt(CADamageTypes.source(level, CADamageTypes.OCEAN_MAGIC),
						(float) attackDamage);
				if (center instanceof LivingEntity attacker && entity instanceof LivingEntity target) {
					SIHelper.causeSanityInjury(target, attacker, attackDamage * 150, SanityEvent.Hurt.Type.ENTITY);
				}
			}
		}
	}
}
