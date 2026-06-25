
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.TideDeathrepellerEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.List;

public class TideBishopSpawneggItem extends ForgeSpawnEggItem {
	public TideBishopSpawneggItem() {
		super(CaerulaArborModEntities.TIDE_BISHOP, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_0"));
		list.add(Component.translatable("item.caerula_arbor.tide_bishop_spawnegg.description_1"));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        if (entity != null) {
            if (entity instanceof TideDeathrepellerEntity) {
                if (!entity.level().isClientSide())
                    entity.discard();
            }
        }
        return true;
	}
}
