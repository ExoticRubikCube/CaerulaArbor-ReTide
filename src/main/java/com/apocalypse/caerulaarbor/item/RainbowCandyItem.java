
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RainbowCandyItem extends Item {
	public RainbowCandyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.5f).alwaysEat().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.rainbow_candy.description_0"));
		list.add(Component.translatable("item.caerula_arbor.rainbow_candy.description_1"));
	}

	@Override
	public @NotNull InteractionResult interactLivingEntity(
			@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
	) {
		if (!(target instanceof Sheep)) {
			return InteractionResult.PASS;
		}
		stack.shrink(1);
		target.setCustomName(Component.literal("jeb_"));
		return InteractionResult.sidedSuccess(player.level().isClientSide());
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(CAItems.PAPER_BAG.get());
		super.finishUsingItem(itemstack, world, entity);
		if (!entity.level().isClientSide()) {
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 280, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 280, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 560, 1));
		}
		boolean _setval = true;
		entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_util_RAINBOW = _setval;
			capability.syncPlayerVariables(entity);
		});
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}
