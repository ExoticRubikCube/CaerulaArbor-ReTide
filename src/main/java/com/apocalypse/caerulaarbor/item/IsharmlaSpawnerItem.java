
package com.apocalypse.caerulaarbor.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;

import java.util.List;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class IsharmlaSpawnerItem extends ForgeSpawnEggItem {
	public IsharmlaSpawnerItem() {
		super(CaerulaArborModEntities.ISHARMLA, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.isharmla_spawner.description_0"));
	}
}
