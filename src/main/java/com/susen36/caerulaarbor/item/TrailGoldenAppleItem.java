
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class TrailGoldenAppleItem extends Item {
	public TrailGoldenAppleItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.8f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        SIHelper.causeSanityInjury(entity, 80, SanityEvent.Hurt.Type.FOOD);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1250, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 2));
			if (world instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY() + 0.8, entity.getZ(), 48, 0.5, 1, 0.5, 0.1);
			}
		}
		return super.finishUsingItem(itemstack, world, entity);
	}
}
