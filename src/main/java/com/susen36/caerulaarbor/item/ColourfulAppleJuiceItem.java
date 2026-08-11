
package com.susen36.caerulaarbor.item;

import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.babel.manager.EPManager;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;


public class ColourfulAppleJuiceItem extends Item {
	public ColourfulAppleJuiceItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 32;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.colourful_apple_juice.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 2));
		}
		EPManager.getEP(entity).getEP(AbstractEPCapability.EPType.NERVOUS).heal(80);
		if (entity instanceof Player) {
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			capability.player_light = Math.min(capability.player_light + 12, 100.0);
			capability.syncPlayerVariables(entity);
		}
		if (!(entity instanceof Player)) {
			resultStack.shrink(1);
			ItemStack emptyCup = new ItemStack(CAItems.OCEANGLASS_CUP.get());
			if (resultStack.isEmpty()) {
				return emptyCup;
			}
		} else if (entity instanceof Player player && !player.getAbilities().instabuild) {
			resultStack.shrink(1);
			ItemStack emptyCup = new ItemStack(CAItems.OCEANGLASS_CUP.get());
			if (resultStack.isEmpty()) {
				return emptyCup;
			}
			if (!player.getInventory().add(emptyCup)) {
				player.drop(emptyCup, false);
			}
		}
		return resultStack;
	}
}