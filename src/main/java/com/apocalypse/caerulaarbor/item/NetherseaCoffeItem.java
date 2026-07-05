
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
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

public class NetherseaCoffeItem extends Item {
	public NetherseaCoffeItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nethersea_coffee.description_0"));
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
            entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY.get(), 400, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 1));
        }
        SIHelper.causeSanityInjury(entity, 45, SanityEvent.Hurt.Type.FOOD);
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
