package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
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
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 3.5F, 1F)
						.particle(ParticleTypes.RAIN, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.player_maxlive = capability.player_maxlive + 3;
		capability.syncPlayerVariables(player);
		PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
		capability2.player_lives = capability2.player_lives + 3;
		capability2.syncPlayerVariables(player);
		ItemStack setstack = new ItemStack(CABlocks.ALLAY_BLOCK.get()).copy();
		setstack.setCount(1);
		ItemHandlerHelper.giveItemToPlayer(player, setstack);
	}
}
