package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;


public class RedstoneIrisFlowerItem extends CollectibleItem.CustomCollectibleItem {
	public RedstoneIrisFlowerItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC), true, 20, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		for (int index0 = 0; index0 < 16; index0++) {
			if (level instanceof ServerLevel serverLevel)
				serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.6, 0.75)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 4));
		}
	}
}
