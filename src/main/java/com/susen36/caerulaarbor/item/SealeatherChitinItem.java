
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.client.model.entity.ModelSealeatherChitinArmor;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class SealeatherChitinItem extends ArmorItem {
	public SealeatherChitinItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 25;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{3, 6, 7, 3}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 15;
			}

			@Override
			public SoundEvent getEquipSound() {
				return SoundEvents.ARMOR_EQUIP_CHAIN;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.OCEAN_CHITIN.get()), new ItemStack(CAItems.OCEAN_PHLOEM.get()));
			}

			@Override
			public String getName() {
				return "sealeather_chitin";
			}

			@Override
			public float getToughness() {
				return 0.5f;
			}

			@Override
			public float getKnockbackResistance() {
				return 0f;
			}
		}, type, properties);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.sealeather_chitin.desc"));
	}


	public static class Helmet extends SealeatherChitinItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties());
		}

		@Override
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
			consumer.accept(new IClientItemExtensions() {
				@Override
				public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
					HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("head", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).helmet, "hat",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
					armorModel.crouching = living.isShiftKeyDown();
					armorModel.riding = defaultModel.riding;
					armorModel.young = living.isBaby();
					return armorModel;
				}
			});
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/entities/chitin_pholem.png";
		}
	}

	@Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID(slot.toString().hashCode(), 0);
        String name = "caerula_arbor_attribute_modifier";
        if (slot == this.getEquipmentSlot()){
            map = HashMultimap.create(map);
            map.put(CAAttributes.MISSRATE.get(),
                    new AttributeModifier(uuid, name , 4f, AttributeModifier.Operation.ADDITION));
            map.put(CAAttributes.SANITY_RESISTANCE.get(),
                    new AttributeModifier(uuid, name , 8f, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

	public static class Chestplate extends SealeatherChitinItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, level, list, flag);
			list.add(Component.translatable("item.caerula_arbor.sealeather_chitin_chestplate.descr"));
		}

		@Override
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
			consumer.accept(new IClientItemExtensions() {
				@Override
				@OnlyIn(Dist.CLIENT)
				public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
					HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("body", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chestplt, "left_arm",
								new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chesarmL, "right_arm",
								new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).chestarmR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
					armorModel.crouching = living.isShiftKeyDown();
					armorModel.riding = defaultModel.riding;
					armorModel.young = living.isBaby();
					return armorModel;
				}
			});
		}

		@Override
    	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        	super.inventoryTick(itemstack, world, entity, slot, selected);
        	if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
        		if (player.hasEffect(CAMobEffects.ESSENCE_RESISTANCE.get())) return;
            	if (player.tickCount % 550 == 64){
                	player.addEffect(
                		new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(),
                		400, 0, false, false)
                		);
            	}
        	}
        }

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/entities/chitin_pholem.png";
		}
	}

	public static class Leggings extends SealeatherChitinItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties());
		}

		@Override
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
			consumer.accept(new IClientItemExtensions() {
				@Override
				@OnlyIn(Dist.CLIENT)
				public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
					HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("left_leg", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).legL, "right_leg",
								new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).legR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
					armorModel.crouching = living.isShiftKeyDown();
					armorModel.riding = defaultModel.riding;
					armorModel.young = living.isBaby();
					return armorModel;
				}
			});
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/entities/chitin_pholem.png";
		}
	}

	public static class Boots extends SealeatherChitinItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties());
		}

		@Override
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
			consumer.accept(new IClientItemExtensions() {
				@Override
				@OnlyIn(Dist.CLIENT)
				public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
					HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("left_leg", new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).bootL, "right_leg",
								new ModelSealeatherChitinArmor(Minecraft.getInstance().getEntityModels().bakeLayer(ModelSealeatherChitinArmor.LAYER_LOCATION)).bootR, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
									"hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
					armorModel.crouching = living.isShiftKeyDown();
					armorModel.riding = defaultModel.riding;
					armorModel.young = living.isBaby();
					return armorModel;
				}
			});
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "caerula_arbor:textures/entities/chitin_pholem.png";
		}
	}
}
