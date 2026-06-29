
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAItems;
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

public class DeepSeagrassJuiceItem extends Item {
	public DeepSeagrassJuiceItem() {
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
		list.add(Component.translatable("item.caerula_arbor.deep_seagrass_juice.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0));
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
