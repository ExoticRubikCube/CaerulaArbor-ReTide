
package com.susen36.caerulaarbor.item;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CAMobEffects;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EnchantedTrailGoldenAppleItem extends Item {
	public EnchantedTrailGoldenAppleItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(1f).alwaysEdible().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 40;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		SIHelper.causeSanityInjury(entity, 120, SanityEvent.Hurt.Type.FOOD);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1750, 4));
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3));
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3));
			entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0));
			entity.addEffect(new MobEffectInstance(CAMobEffects.SANITY_IMMUE, 2400, 0));
			entity.addEffect(new MobEffectInstance(BabelMobEffects.ESSENCE_RESISTANCE, 3600, 1));
		}
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 0.8, z, 48, 0.5, 1, 0.5, 0.1);
		}
		return retval;
	}
}