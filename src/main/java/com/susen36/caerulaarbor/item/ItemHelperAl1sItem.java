package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.susen36.caerulaarbor.init.CASounds;

import java.util.List;


public class ItemHelperAl1sItem extends LittleHelperItem {
	public ItemHelperAl1sItem() {
		super(Rarity.RARE);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		list.add(Component.translatable("item.caerula_arbor.item_helper_al_1s.description_0"));
		list.add(Component.translatable("item.caerula_arbor.item_helper_al_1s.description_1"));
		list.add(Component.translatable("item.caerula_arbor.item_helper_al_1s.description_2"));
		list.add(Component.translatable("item.caerula_arbor.item_helper_al_1s.description_3"));
	}

	@Override
	protected EntityType<?> getHelperEntityType() {
		return CAEntities.AL_1_S_HELPER.get();
	}

	@Override
	protected void playPlaceSound(Level level, BlockPos soundPos) {
		level.playSound(null, soundPos, CASounds.AL1S_DEP.get(), SoundSource.BLOCKS, 3, 1);
	}
}