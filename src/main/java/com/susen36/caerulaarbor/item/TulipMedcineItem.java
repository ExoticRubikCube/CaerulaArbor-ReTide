package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class TulipMedcineItem extends CollectibleItem.CustomCollectibleItem {
	public TulipMedcineItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(2f).alwaysEdible().build()), false, 25, false, CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 1, 0),
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
		if (level instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(ParticleTypes.CLOUD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.15);
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.disoclusion = 0;
		capability.syncPlayerVariables(player);
		if (!level.isClientSide()) {
			player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 2));
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 1));
			player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
			player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 2));
		}
		if (player instanceof ServerPlayer serverPlayer) {
			AdvancementHolder adv = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "but_i_refuse"));
			AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
			if (!ap.isDone()) {
				for (String criteria : ap.getRemainingCriteria())
					serverPlayer.getAdvancements().award(adv, criteria);
			}
		}
	}
}
