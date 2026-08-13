package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class RainbowCandyItem extends CollectibleItem.CustomCollectibleItem {
	public RainbowCandyItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.5f).alwaysEdible().build()), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public @NotNull InteractionResult interactLivingEntity(
			@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
	) {
		if (!(target instanceof Sheep)) {
			return InteractionResult.PASS;
		}
		stack.shrink(1);
		target.setCustomName(Component.literal("jeb_"));
		return InteractionResult.sidedSuccess(player.level().isClientSide());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide()) {
			player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 280, 1));
			player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 280, 1));
			player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 560, 1));
		}
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.player_util_RAINBOW = true;
		capability.syncPlayerVariables(player);
	}
}
