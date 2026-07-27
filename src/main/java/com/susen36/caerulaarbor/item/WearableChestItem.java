package com.susen36.caerulaarbor.item;

import com.google.common.collect.Iterables;
import com.susen36.caerulaarbor.util.RelicUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

public abstract class WearableChestItem extends ArmorItem {
	public WearableChestItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 2,
				ArmorItem.Type.CHESTPLATE, 11,
				ArmorItem.Type.LEGGINGS, 5,
				ArmorItem.Type.BOOTS, 2
			),
			16,
			SoundEvents.ARMOR_EQUIP_NETHERITE,
			() -> Ingredient.of(),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "wearable_chest"))),
			2.5f,
			0.2f
		), type, properties);
	}

	public static class Chestplate extends WearableChestItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/models/armor/kingarmor_layer_1.png";
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return true;
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				RelicUtils.gainArmor(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
			}
		}
	}
}