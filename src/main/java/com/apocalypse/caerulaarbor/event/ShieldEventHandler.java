package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ShieldEventHandler {
	@SubscribeEvent
	public static void whenEntityBlocksWithShield(ShieldBlockEvent event) {
		Entity blocker = event.getEntity();
		Entity attacker = event.getDamageSource().getEntity();
		if (blocker == null || attacker == null)
			return;
		ItemStack activeItem = blocker instanceof LivingEntity livingBlocker ? livingBlocker.getUseItem() : ItemStack.EMPTY;
		double blockedDamage = event.getBlockedDamage();
		if (activeItem.getItem() == CaerulaArborModItems.COMPLEX_CHITIN_SHIELD.get()) {
			if (attacker instanceof LivingEntity livingAttacker && blocker instanceof LivingEntity livingBlocker) {
				SIHelper.causeSanityInjury(livingAttacker, livingBlocker, Math.min(blockedDamage * 2, 333), SanityEvent.Hurt.Type.ENTITY);
			}
			if (blocker.level() instanceof ServerLevel serverLevel)
				serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, attacker.getX(), attacker.getY() + 0.5, attacker.getZ(), 8, 0.5, 0.5, 0.5, 0.1);
		} else if (activeItem.getItem() == CaerulaArborModItems.TIDELINKED_SHIELD.get()) {
			if (attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get())) {
				MobEffectInstance lessArmorEffect = livingAttacker.getEffect(CaerulaArborModMobEffects.LESS_ARMOR.get());
				double lessArmorAmplifier = lessArmorEffect != null ? lessArmorEffect.getAmplifier() + 1 : 0;
				activeItem.setDamageValue((int) (activeItem.getDamageValue() - lessArmorAmplifier));
				if (blocker instanceof LivingEntity livingBlocker && blocker.isAlive() && livingBlocker.getHealth() < livingBlocker.getMaxHealth())
					livingBlocker.setHealth((float) (livingBlocker.getHealth() + livingBlocker.getMaxHealth() * lessArmorAmplifier * 0.01));
			}
			EntityUtils.giveLessArmor(attacker, 5);
		}
	}
}
