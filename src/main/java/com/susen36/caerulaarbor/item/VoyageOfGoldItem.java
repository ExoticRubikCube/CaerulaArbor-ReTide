package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class VoyageOfGoldItem extends CollectibleItem.CustomCollectibleItem {
	public VoyageOfGoldItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, false, CollectibleTiers.NORMAL, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		for (int index0 = 0; index0 < 8; index0++) {
			if (level instanceof ServerLevel serverLevel)
				serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, (player.getX() + Mth.nextDouble(RandomSource.create(), -1, 1)), (player.getY() + Mth.nextDouble(RandomSource.create(), 0.6, 0.75)), (player.getZ() + Mth.nextDouble(RandomSource.create(), -1, 1)), 4));
		}
		if (!level.isClientSide())
			player.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 400, 1, false, false));
	}
}
