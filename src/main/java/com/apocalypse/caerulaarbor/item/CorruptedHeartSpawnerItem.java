
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.SkadiCorruptedEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.List;

public class CorruptedHeartSpawnerItem extends ForgeSpawnEggItem {
	public CorruptedHeartSpawnerItem() {
		super(CaerulaArborModEntities.SKADI_CORRUPTED, -1, -1, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.corrupted_heart_spawner.description_0"));
		list.add(Component.translatable("item.caerula_arbor.corrupted_heart_spawner.description_1"));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        if ((Entity) entity instanceof SkadiCorruptedEntity _datEntSetI)
            _datEntSetI.getEntityData().set(SkadiCorruptedEntity.DATA_deal, 999999);
        return true;
	}
}
