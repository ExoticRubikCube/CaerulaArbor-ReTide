
package com.susen36.caerulaarbor.item;

import com.google.common.collect.Iterables;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;


public abstract class SealeatherChitinItem extends ArmorItem {
	public SealeatherChitinItem(ArmorItem.Type type, Item.Properties properties) {
		super(Holder.direct(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 3,
				ArmorItem.Type.CHESTPLATE, 7,
				ArmorItem.Type.LEGGINGS, 6,
				ArmorItem.Type.BOOTS, 3
			),
			15,
			SoundEvents.ARMOR_EQUIP_CHAIN,
			() -> Ingredient.of(new ItemStack(CAItems.OCEAN_CHITIN.get()), new ItemStack(CAItems.OCEAN_PHLOEM.get())),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "sealeather_chitin"))),
			0.5f,
			0f
		)), type, properties.component(DataComponents.ATTRIBUTE_MODIFIERS,
			ItemAttributeModifiers.builder()
				.add(CAAttributes.MISSRATE,
					new AttributeModifier(
						ResourceLocation.fromNamespaceAndPath("caerula_arbor", "sealeather_chitin_missrate"),
						4.0,
						AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.add(CAAttributes.SANITY_RESISTANCE,
					new AttributeModifier(
						ResourceLocation.fromNamespaceAndPath("caerula_arbor", "sealeather_chitin_sanity_resistance"),
						8.0,
						AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.sealeather_chitin.desc"));
	}


	public static class Helmet extends SealeatherChitinItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties());
		}
	}

	public static class Chestplate extends SealeatherChitinItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, context, list, flag);
			list.add(Component.translatable("item.caerula_arbor.sealeather_chitin_chestplate.descr"));
		}

		@Override
    	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        	super.inventoryTick(itemstack, world, entity, slot, selected);
        	if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
        		if (player.hasEffect(CAMobEffects.ESSENCE_RESISTANCE)) return;
            	if (player.tickCount % 550 == 64){
                	player.addEffect(
                		new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE,
                		400, 0, false, false)
                		);
            	}
        	}
        }
	}

	public static class Leggings extends SealeatherChitinItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties());
		}
	}

	public static class Boots extends SealeatherChitinItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties());
		}
	}
}