
package com.susen36.caerulaarbor.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BannedItemItem extends Item {
	public BannedItemItem() {
		super(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.banned_item.description_0"));
		list.add(Component.translatable("item.caerula_arbor.banned_item.description_1"));
		list.add(Component.translatable("item.caerula_arbor.banned_item.description_2"));
	}

	@Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pTarget, @NotNull InteractionHand pHand
    ) {
        if (pTarget.getPersistentData().getBoolean("seabornForgive")) {
            return InteractionResult.PASS;
        }
        pTarget.getPersistentData().putBoolean("seabornForgive", true);
        return InteractionResult.SUCCESS;
    }
}
