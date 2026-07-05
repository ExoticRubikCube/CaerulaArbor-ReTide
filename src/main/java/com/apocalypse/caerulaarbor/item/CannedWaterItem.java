
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
public class CannedWaterItem extends Item {
	public CannedWaterItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
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
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
        if (!(entity instanceof Player)) {
            resultStack.shrink(1);
            ItemStack emptyCan = new ItemStack(CAItems.EMPTY_CAN.get());
            if (resultStack.isEmpty()) {
                return emptyCan;
            }
        } else if (entity instanceof Player player && !player.getAbilities().instabuild) {
            resultStack.shrink(1);
            ItemStack emptyCan = new ItemStack(CAItems.EMPTY_CAN.get());
            if (resultStack.isEmpty()) {
                return emptyCan;
            }
            if (!player.getInventory().add(emptyCan)) {
                player.drop(emptyCan, false);
            }
        }
        return resultStack;
	}
}
