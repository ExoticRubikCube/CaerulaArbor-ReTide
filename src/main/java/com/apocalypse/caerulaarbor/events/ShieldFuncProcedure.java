package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ShieldFuncProcedure {
	@SubscribeEvent
	public static void whenEntityBlocksWithShield(ShieldBlockEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity(), event.getDamageSource().getEntity(), event.getBlockedDamage());
		}
	}

    private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity, double blockedamount) {
		if (entity == null || sourceentity == null)
			return;
		double amplifi = 0;
		if ((entity instanceof LivingEntity _entUseItem0 ? _entUseItem0.getUseItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.COMPLEX_CHITIN_SHIELD.get()) {
			EntityUtils.deductSanity(sourceentity, Math.min(blockedamount * 2, 333));
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (sourceentity.getX()), (sourceentity.getY() + 0.5), (sourceentity.getZ()), 8, 0.5, 0.5, 0.5, 0.1);
		} else if ((entity instanceof LivingEntity _entUseItem6 ? _entUseItem6.getUseItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.TIDELINKED_SHIELD.get()) {
			if (sourceentity instanceof LivingEntity _livEnt8 && _livEnt8.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get())) {
				amplifi = (sourceentity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) + 1;
				(entity instanceof LivingEntity _entUseItem12 ? _entUseItem12.getUseItem() : ItemStack.EMPTY)
						.setDamageValue((int) ((entity instanceof LivingEntity _entUseItem10 ? _entUseItem10.getUseItem() : ItemStack.EMPTY).getDamageValue() - amplifi));
				if (entity.isAlive() && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
					if (entity instanceof LivingEntity _entity)
						_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * amplifi * 0.01));
				}
			}
			EntityUtils.giveLessArmor(sourceentity, 5);
		}
	}
}
