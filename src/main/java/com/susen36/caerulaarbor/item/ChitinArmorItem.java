
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.UUID;

public abstract class ChitinArmorItem extends ArmorItem {
	public ChitinArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 29;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{2, 5, 7, 3}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 11;
			}

			@Override
			public SoundEvent getEquipSound() {
				return SoundEvents.ARMOR_EQUIP_IRON;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.OCEAN_CHITIN.get()));
			}

			@Override
			public String getName() {
				return "chitin_armor";
			}

			@Override
			public float getToughness() {
				return 1.5f;
			}

			@Override
			public float getKnockbackResistance() {
				return 0.15f;
			}
		}, type, properties);
	}

	@Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID(slot.toString().hashCode(), 0);
        String name = "caerula_arbor_attribute_modifier";
        if (slot == this.getEquipmentSlot()){
            map = HashMultimap.create(map);
            map.put(CAAttributes.SANITY_RESISTANCE.get(),
                    new AttributeModifier(uuid, name , 7.5f, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

	public static class Helmet extends ChitinArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/models/armor/chiitnarmor_layer_1.png";
		}
	}

	public static class Chestplate extends ChitinArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/models/armor/chiitnarmor_layer_1.png";
		}
	}

	public static class Leggings extends ChitinArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/models/armor/chiitnarmor_layer_2.png";
		}
	}

	public static class Boots extends ChitinArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties());
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/models/armor/chiitnarmor_layer_1.png";
		}
	}
}
