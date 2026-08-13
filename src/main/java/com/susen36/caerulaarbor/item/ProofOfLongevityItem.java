package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class ProofOfLongevityItem extends CollectibleItem.CustomCollectibleItem {
	public ProofOfLongevityItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD.value(), 3.5F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.player_maxlive = capability.player_maxlive + 6;
		capability.syncPlayerVariables(player);
		PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
		capability2.player_lives = capability2.player_lives + 6;
		capability2.syncPlayerVariables(player);
	}
}
