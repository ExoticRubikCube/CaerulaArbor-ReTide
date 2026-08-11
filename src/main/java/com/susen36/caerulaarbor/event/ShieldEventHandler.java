package com.susen36.caerulaarbor.event;

import com.susen36.babel.effect.LessArmorMobEffect;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber
public class ShieldEventHandler {
	@SubscribeEvent
	public static void whenEntityBlocksWithShield(LivingShieldBlockEvent event) {
		Entity blocker = event.getEntity();
		Entity attacker = event.getDamageSource().getEntity();
		if (attacker == null)
			return;
		ItemStack activeItem = blocker instanceof LivingEntity livingBlocker ? livingBlocker.getUseItem() : ItemStack.EMPTY;
		double blockedDamage = event.getBlockedDamage();
		if (activeItem.getItem() == CAItems.COMPLEX_CHITIN_SHIELD.get()) {
			if (attacker instanceof LivingEntity livingAttacker && blocker instanceof LivingEntity livingBlocker) {
				EPUtils.causeSanityInjury(livingAttacker, livingBlocker, Math.min(blockedDamage * 2, 333));
			}
			if (blocker.level() instanceof ServerLevel serverLevel)
				serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, attacker.getX(), attacker.getY() + 0.5, attacker.getZ(), 8, 0.5, 0.5, 0.5, 0.1);
		} else if (activeItem.getItem() == CAItems.TIDELINKED_SHIELD.get()) {
			if (attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(BabelMobEffects.LESS_ARMOR)) {
				MobEffectInstance lessArmorEffect = livingAttacker.getEffect(BabelMobEffects.LESS_ARMOR);
				double lessArmorAmplifier = lessArmorEffect != null ? lessArmorEffect.getAmplifier() + 1 : 0;
				activeItem.setDamageValue((int) (activeItem.getDamageValue() - lessArmorAmplifier));
				if (blocker instanceof LivingEntity livingBlocker && blocker.isAlive() && livingBlocker.getHealth() < livingBlocker.getMaxHealth())
					livingBlocker.setHealth((float) (livingBlocker.getHealth() + livingBlocker.getMaxHealth() * lessArmorAmplifier * 0.01));
			}
			if (attacker instanceof LivingEntity livingAttacker)
				LessArmorMobEffect.apply(livingAttacker);
		}
	}
}