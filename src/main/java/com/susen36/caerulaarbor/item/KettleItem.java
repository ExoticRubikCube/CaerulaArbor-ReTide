package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;


public class KettleItem extends CollectibleItem.CustomCollectibleItem {
	public KettleItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), true, 25, false, CollectibleTiers.NORMAL, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
        capability.player_maxlive = capability.player_maxlive + 1;
		capability.syncPlayerVariables(player);
		PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
        capability2.player_lives = capability2.player_lives + 1;
		capability2.syncPlayerVariables(player);
		ItemStack setstack = new ItemStack(CABlocks.BLOCK_KETTLE.get()).copy();
		setstack.setCount(1);
		ItemHandlerHelper.giveItemToPlayer(player, setstack);
		for (int index0 = 0; index0 < 2; index0++) {
			if (level instanceof ServerLevel serverLevel)
				serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, x, y, z, 4));
		}
	}
}
