package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class TrailAppleItem extends Item {
	public TrailAppleItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(5).saturationModifier(0.35f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		EPUtils.causeSanityInjury(entity, 4);
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 0.8, z, 48, 0.5, 1, 0.5, 0.1);
		}
		return super.finishUsingItem(itemstack, world, entity);
	}
}