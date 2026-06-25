package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PEVOHealFuncProcedure {
	@SubscribeEvent
	public static void onEntityHealed(LivingHealEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity(), event.getAmount());
	}

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, double amount) {
		if (entity == null)
			return;
		double h = 0;
		double mh = 0;
		double overflow = 0;
		double rate = 0;
		double lvl = 0;
		double dRate = 0;
		double d = 0;
		double angle = 0;
        if (event != null && event.isCanceled()) {
            return;
        }
        if (entity.isShiftKeyDown() && entity instanceof Player && EntityUtils.canPlayerEvo(entity)) {
			h = entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1;
			mh = entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1;
			overflow = (h + amount) - mh;
			if (overflow > 0) {
				lvl = EntityUtils.getNodeHealDamage(entity);
				if (lvl >= 4) {
					rate = 1;
					dRate = 20;
				} else if (lvl >= 3) {
					rate = 0.6;
					dRate = 10;
				} else if (lvl >= 2) {
					rate = 0.3;
				} else if (lvl >= 1) {
					rate = 0.1;
				}
				if (rate > 0) {
					for (int index0 = 0; index0 < 120; index0++) {
						angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
						d = Mth.nextDouble(RandomSource.create(), 2.75, 3.25);
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.HAPPY_VILLAGER, (x + d * Math.sin(angle)), (y + 0.4), (z + d * Math.cos(angle)), 1, 0.1, 0.1, 0.1, 0.1);
					}
					rate = overflow * rate;
					dRate = overflow * dRate;
					for (Entity entityiterator : world.getEntities(entity, new AABB((x - 3), (y - 1), (z - 3), (x + 3), (y + 3), (z + 3)))) {
						if ((entityiterator != null ? entity.distanceTo(entityiterator) : -1) <= 3) {
							if (entityiterator instanceof LivingEntity) {
								if (!(entityiterator instanceof Monster)) {
									if (!((entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entity)) {
										continue;
									}
								}
								entityiterator.hurt(
										new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "wipe_magic"))), entity),
										(float) rate);
								if (dRate > 0) {
									EntityUtils.deductSanity(entityiterator, dRate);
								}
							}
						}
					}
				}
			}
		}
	}
}
