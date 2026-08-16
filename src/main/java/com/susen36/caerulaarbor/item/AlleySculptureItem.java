package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.babel.util.LifePointUtils;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;


public class AlleySculptureItem extends CollectibleItem.CustomCollectibleItem {
	public AlleySculptureItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), 25, false, CollectibleTiers.NORMAL, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 3.5F, 1F)
						.particle(ParticleTypes.RAIN, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		LifePointUtils.setMaxLifePoint(player, LifePointUtils.getMaxLifePoint(player) + 3);
		LifePointUtils.setLifePoint(player, LifePointUtils.getLifePoint(player) + 3);
		ItemStack setstack = new ItemStack(CABlocks.ALLAY_BLOCK.get()).copy();
		setstack.setCount(1);
		ItemHandlerHelper.giveItemToPlayer(player, setstack);
	}
}
