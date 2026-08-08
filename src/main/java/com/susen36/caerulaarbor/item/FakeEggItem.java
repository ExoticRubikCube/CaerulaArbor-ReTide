package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class FakeEggItem extends Item {
	public FakeEggItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.25f).build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 35;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if ((Entity) entity instanceof LivingEntity livingEntity) {
            SIHelper.causeSanityInjury(livingEntity, 160, SanityEvent.Hurt.Type.FOOD);
        }
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 0.8, z, 48, 0.5, 1, 0.5, 0.1);
        }
        return retval;
	}
}