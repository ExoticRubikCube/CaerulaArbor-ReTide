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


public class PiglinDiaryItem extends CollectibleItem.CustomCollectibleItem {
	public PiglinDiaryItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.AMETHYST_BLOCK_RESONATE, 2F, 1F)
						.particle(ParticleTypes.NAUTILUS, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
        capability.player_shield = capability.player_shield + 4;
		capability.syncPlayerVariables(player);
	}
}
