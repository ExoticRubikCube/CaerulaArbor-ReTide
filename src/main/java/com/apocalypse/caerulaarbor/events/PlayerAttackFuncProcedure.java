package com.apocalypse.caerulaarbor.events;

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
public class PlayerAttackFuncProcedure {
	@SubscribeEvent
	public static void onPlayerCriticalHit(CriticalHitEvent event) {
		execute(event.getEntity(), event.isVanillaCritical());
	}

    private static void execute(Entity sourceentity, boolean isvanillacritical) {
		if (sourceentity == null)
			return;
		ItemStack item_temp;
		if (isvanillacritical) {
			item_temp = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy();
			if ((sourceentity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_BARREN) {
				if (item_temp.getItem() instanceof AxeItem || item_temp.is(ItemTags.create(new ResourceLocation("minecraft:axes")))) {
					if (sourceentity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get())) {
						if ((sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get()).getAmplifier() : 0) < 7) {
							if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BUTCHERS_POWER.get(), 160,
                                        (sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.BUTCHERS_POWER.get()).getAmplifier() : 0)
                                                + 1,
										false, false));
						} else {
							if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BUTCHERS_POWER.get(), 160, 7, false, false));
						}
					} else {
						if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BUTCHERS_POWER.get(), 160, 0, false, false));
					}
				}
			}
		}
	}
}
