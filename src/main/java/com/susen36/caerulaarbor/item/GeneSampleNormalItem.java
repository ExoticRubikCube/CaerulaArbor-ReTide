
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class GeneSampleNormalItem extends Item {
	public GeneSampleNormalItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.gene_sample_normal.description_0"));
		list.add(Component.translatable("item.caerula_arbor.gene_sample_normal.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        EntityUtils.givePlayerReserve(world, entity.getX(), entity.getY(), entity.getZ(), entity, super.use(world, entity, hand).getObject());
		return super.use(world, entity, hand);
	}
}