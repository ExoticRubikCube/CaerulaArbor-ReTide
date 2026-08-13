package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class GuardianStareItem extends CollectibleItem.CustomCollectibleItem {
	public GuardianStareItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), false, 25, CollectibleTiers.NORMAL, 0, 1, 0,
				CollectibleActivation.builder()
						.sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
						.particle(ParticleTypes.NAUTILUS, 72)
						.showOverlay(true)
						.build());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		String hoverText = null;
        if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            list.add(Component.translatable("item.caerula_arbor.relics.used"));
        }
    }

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		player.giveExperienceLevels(4);
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                living.removeEffect(MobEffects.DIG_SLOWDOWN);
        }
    }
}
