
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class RadiantBerriesItem extends Item {
	public RadiantBerriesItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(5).saturationMod(1f).alwaysEat().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 40;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        EntityUtils.restorePlayerLights(entity, 24);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3200, 2));
			entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0));
			entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE, 3600, 1));
		}
		entity.removeEffect(MobEffects.BLINDNESS);
		return super.finishUsingItem(itemstack, world, entity);
	}
}