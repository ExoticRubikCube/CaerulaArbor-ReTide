package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class CollectibleCursedRESEARCHItem extends CollectibleItem.CustomCollectibleItem {
	public CollectibleCursedRESEARCHItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, CollectibleTiers.CURSED, 0, 1, 0,
				CollectibleActivation.forTier(CollectibleTiers.CURSED));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		String hoverText = null;
		if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
			list.add(Component.translatable("item.caerula_arbor.cursed.used"));
		}
    }

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
	}
}
