package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

@EventBusSubscriber
public class PlayerAttackEventHandler {
	@SubscribeEvent
	public static void onPlayerCriticalHit(CriticalHitEvent event) {
		LivingEntity attacker = event.getEntity();
		if (!event.isVanillaCritical()) {
			return;
		}

		if (!Relic.HAND_OF_PULVERIZATION.gained(attacker)) {
			return;
		}

		ItemStack mainHandItem = attacker.getMainHandItem().copy();
		if (!(mainHandItem.getItem() instanceof AxeItem) && !mainHandItem.is(ItemTags.create(ResourceLocation.parse("minecraft:axes")))) {
			return;
		}

		if (!attacker.level().isClientSide()) {
			MobEffectInstance currentButchersPower = attacker.getEffect(CAMobEffects.BUTCHERS_POWER);
			int nextAmplifier = currentButchersPower == null ? 0 : Math.min(currentButchersPower.getAmplifier() + 1, 7);
			attacker.addEffect(new MobEffectInstance(CAMobEffects.BUTCHERS_POWER, 160, nextAmplifier, false, false));
		}
	}
}