package com.apocalypse.caerulaarbor.event;

import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerAttackEventHandler {
	@SubscribeEvent
	public static void onPlayerCriticalHit(CriticalHitEvent event) {
		Entity attackerEntity = event.getEntity();
		if (!event.isVanillaCritical() || !(attackerEntity instanceof LivingEntity attacker)) {
			return;
		}

		if (!(attacker.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
				.orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_BARREN) {
			return;
		}

		ItemStack mainHandItem = attacker.getMainHandItem().copy();
		if (!(mainHandItem.getItem() instanceof AxeItem) && !mainHandItem.is(ItemTags.create(new ResourceLocation("minecraft:axes")))) {
			return;
		}

		if (!attacker.level().isClientSide()) {
			MobEffectInstance currentButchersPower = attacker.getEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get());
			int nextAmplifier = currentButchersPower == null ? 0 : Math.min(currentButchersPower.getAmplifier() + 1, 7);
			attacker.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BUTCHERS_POWER.get(), 160, nextAmplifier, false, false));
		}
	}
}
