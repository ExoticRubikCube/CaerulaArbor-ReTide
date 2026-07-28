package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public abstract class ChitinArmorItem extends ArmorItem {
	public ChitinArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(Holder.direct(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 3,
				ArmorItem.Type.CHESTPLATE, 7,
				ArmorItem.Type.LEGGINGS, 5,
				ArmorItem.Type.BOOTS, 2
			),
			11,
			SoundEvents.ARMOR_EQUIP_IRON,
			() -> Ingredient.of(new ItemStack(CAItems.OCEAN_CHITIN.get())),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "chiitnarmor"))),
			1.5f,
			0.15f
		)), type, properties.component(DataComponents.ATTRIBUTE_MODIFIERS,
			ItemAttributeModifiers.builder()
				.add(CAAttributes.SANITY_RESISTANCE,
					new AttributeModifier(
						ResourceLocation.fromNamespaceAndPath("caerula_arbor", "chitin_armor_sanity_resistance"),
						7.5,
						AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.build()));
	}

	public static class Helmet extends ChitinArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties());
		}

	}

	public static class Chestplate extends ChitinArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties());
		}

	}

	public static class Leggings extends ChitinArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties());
		}

	}

	public static class Boots extends ChitinArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties());
		}
	}
}