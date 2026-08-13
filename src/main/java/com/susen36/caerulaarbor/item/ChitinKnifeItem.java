package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class ChitinKnifeItem extends CollectibleItem.CustomCollectibleItem {
	public ChitinKnifeItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), false, 25, false, CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.BELL_BLOCK, 3.5F, 1F)
						.particle(ParticleTypes.DOLPHIN, 72)
						.showOverlay(true)
						.build());
	}

	

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		if (level instanceof ServerLevel serverLevel) {
			ItemEntity entityToSpawn = new ItemEntity(serverLevel, x, y, z, new ItemStack(CAItems.OCEAN_TRIM_TEMPLATE.get()));
			entityToSpawn.setPickUpDelay(5);
			entityToSpawn.setUnlimitedLifetime();
			serverLevel.addFreshEntity(entityToSpawn);
		}
	}
}
