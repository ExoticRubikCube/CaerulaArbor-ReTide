package com.susen36.caerulaarbor.item;

import com.susen36.babel.api.entity.ElementalAttacker;
import com.susen36.babel.elemental.base.AbstractEPCapability;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class TrailriteArmorItem extends ArmorItem implements GeoItem, SyncedAnimationItem, ElementalAttacker {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public TrailriteArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(Holder.direct(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 6,
				ArmorItem.Type.CHESTPLATE, 10,
				ArmorItem.Type.LEGGINGS, 9,
				ArmorItem.Type.BOOTS, 5
			),
			22,
			SoundEvents.ARMOR_EQUIP_NETHERITE,
			() -> Ingredient.of(new ItemStack(CAItems.TRAILRITE.get())),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_armor"))),
			6f,
			0.2f
		)), type, properties.component(DataComponents.ATTRIBUTE_MODIFIERS,
			ItemAttributeModifiers.builder()
				.add(CAAttributes.SANITY_RESISTANCE,
					new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_sanity_resistance"), 17.5, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.add(CAAttributes.GENERAL_DEFENSE,
					new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_general_defense"), 3.5, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.add(CAAttributes.MAGIC_RESISTANCE,
					new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_magic_resistance"), 8.0, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.build()));
	}

	@Override
	public AbstractEPCapability.EPType getElementalType() {
		return AbstractEPCapability.EPType.NERVOUS;
	}

	@Override
	public double getElementalRate() {
		return 0.75;
	}

	@Override
	public double getElementalInjuryDamage() {
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		list.add(Component.translatable("item.caerula_arbor.sealeather_chitin.desc"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_0"));
        if(Screen.hasShiftDown()){
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_2"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_3"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_4"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_5"));
        } else {
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_1"));
        }
		super.appendHoverText(itemstack, context, list, flag);
	}

	@Override
    public void setDamage(ItemStack stack, int damage){
        super.setDamage(stack,Math.min(damage,this.getDamage(stack)+1));
    }

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return Math.min(amount, 1);
	}

	private PlayState predicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.trairite_armor.idle"));
			Entity entity = (Entity) event.getData(DataTickets.ENTITY);
			if (entity instanceof ArmorStand) {
				return PlayState.CONTINUE;
			}
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
			Entity entity = (Entity) event.getData(DataTickets.ENTITY);
			if (entity instanceof ArmorStand) {
				return PlayState.CONTINUE;
			}
			return PlayState.CONTINUE;
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "controller", 5, this::predicate));
		data.add(new AnimationController<>(this, "procedureController", 5, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}