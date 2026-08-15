package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.babel.util.EPUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class CollectibleCursedGLOWBODYItem extends CollectibleItem.CustomCollectibleItem {
	public CollectibleCursedGLOWBODYItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, false, CollectibleTiers.CURSED, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.forTier(CollectibleTiers.CURSED));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.EAT;
	}

	

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		if (!level.isClientSide())
			player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
		if (level instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY(), player.getZ(), 72, 1, 2, 1, 0.1);
		EPUtils.causeSanityInjury(player, 25);
		player.getCooldowns().addCooldown(stack.getItem(), 200);
	}
}
