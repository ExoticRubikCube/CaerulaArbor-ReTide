
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.TidelinkedImmortalEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.List;


public class TideBishopSpawneggItem extends DeferredSpawnEggItem {
	public TideBishopSpawneggItem() {
		super(CAEntities.TIDE_BISHOP, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_0"));
		list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_1"));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        if (!entity.level().isClientSide() && entity instanceof TidelinkedImmortalEntity) {
            entity.discard();
        }
        return true;
	}
}