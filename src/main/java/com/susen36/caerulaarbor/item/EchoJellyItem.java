
package com.susen36.caerulaarbor.item;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class EchoJellyItem extends Item {
	public EchoJellyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE).food((new FoodProperties.Builder()).nutrition(9).saturationModifier(0.75f).alwaysEdible().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 20;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.echo_jelly.description_0"));
		list.add(Component.translatable("item.caerula_arbor.echo_jelly.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        entity.clearFire();
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 80, 0));
			entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY, 700, 2));
			entity.addEffect(new MobEffectInstance(BabelMobEffects.ESSENCE_RESISTANCE, 3600, 1));
			entity.addEffect(new MobEffectInstance(CAMobEffects.SANITY_HEAL, 1, 2, false, false));
		}
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		double setval = Math.min(capability.player_light + 19, 100);
		capability.player_light = setval;
		capability.syncPlayerVariables(entity);
		return super.finishUsingItem(itemstack, world, entity);
	}
}