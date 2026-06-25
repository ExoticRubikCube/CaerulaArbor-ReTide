package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.entity.ChiselerFishEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
//TODO:写入实体类ChiselerFishEntity
@Mod.EventBusSubscriber
public class LockFuncProcedure {
	@SubscribeEvent
	public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
		execute(event, event.getEntity());
	}

    private static void execute(@Nullable Event event, Entity sourceentity) {
		if (sourceentity == null)
			return;
		if (sourceentity instanceof ChiselerFishEntity livEnt1 && !livEnt1.hasEffect(CaerulaArborModMobEffects.COOLDOWN_SINAL.get())) {
			if (!livEnt1.level().isClientSide())
				livEnt1.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3, false, false));
			if (!livEnt1.level().isClientSide())
				livEnt1.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1));
			if (!livEnt1.level().isClientSide())
				livEnt1.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.COOLDOWN_SINAL.get(), 800, 0, false, false));
		}
	}
}
