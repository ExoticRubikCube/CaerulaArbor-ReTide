package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class OilAndCreamItem extends CollectibleItem.CustomCollectibleItem {
	public OilAndCreamItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(1).saturationModifier(0f).alwaysEdible().build()), false, 25, false, CollectibleTiers.ADVANCED, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.HAPPY_VILLAGER, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(CAItems.EMPTY_CAN.get());
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.disoclusion = 0;
		capability.syncPlayerVariables(player);
		PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
        capability2.player_light = Mth.clamp(capability2.player_light - 30, 0, Double.MAX_VALUE);
		capability2.syncPlayerVariables(player);
		player.hurt(player.level().damageSources().inFire(), 12);
		if (!level.isClientSide()) {
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
			player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
			player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
			player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1200, 0));
		}
		player.igniteForSeconds(8);
		if (level instanceof ServerLevel serverLevel) {
			ItemEntity entityToSpawn = new ItemEntity(serverLevel, x, y, z, new ItemStack(Items.STICK));
			entityToSpawn.setPickUpDelay(10);
			serverLevel.addFreshEntity(entityToSpawn);
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
