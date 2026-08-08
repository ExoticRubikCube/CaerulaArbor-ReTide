
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class OceanEyeItem extends Item {
	public OceanEyeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(1.5f).build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 40;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.ocean_eye.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		SIHelper.causeSanityInjury(entity, 325, SanityEvent.Hurt.Type.FOOD);
		if (world instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 72, 1, 2, 1, 0.1);
		}
		return retval;
	}
}