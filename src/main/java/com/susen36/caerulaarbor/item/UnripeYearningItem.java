package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class UnripeYearningItem extends CollectibleItem.CustomCollectibleItem {
	public UnripeYearningItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), false, 25, CollectibleTiers.ADVANCED, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, 2F, 1F)
						.particle(ParticleTypes.DOLPHIN, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
	}
}
