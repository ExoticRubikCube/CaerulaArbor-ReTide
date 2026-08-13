package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;


public class OddFluteItem extends CollectibleItem.CustomCollectibleItem {
	public OddFluteItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), false, 40, CollectibleTiers.RARE, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(CASounds.FLUTESONG.get(), 2F, 1F)
						.particle(ParticleTypes.NOTE, 72)
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
		for (int index0 = 0; index0 < 7; index0++) {
			if (level instanceof ServerLevel serverLevel)
				serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.6, 0.75)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 4));
		}
		if (!level.isClientSide())
			player.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 300, 0, false, false));
	}
}
