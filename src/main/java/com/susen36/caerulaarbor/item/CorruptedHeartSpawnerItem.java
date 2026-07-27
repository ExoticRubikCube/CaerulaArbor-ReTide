
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.SkadiCorruptedEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.List;


public class CorruptedHeartSpawnerItem extends DeferredSpawnEggItem {
	public CorruptedHeartSpawnerItem() {
		super(CAEntities.SKADI_CORRUPTED, -1, -1, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.corrupted_heart_spawner.description_0"));
		list.add(Component.translatable("item.caerula_arbor.corrupted_heart_spawner.description_1"));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        if (entity instanceof SkadiCorruptedEntity datEntSetI)
            datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_DEAL, 999999);
        return true;
	}
}