
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.anim.SyncedAnimationItem;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
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


public class KnightIronItem extends ArmorItem implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public KnightIronItem(ArmorItem.Type type, Item.Properties properties) {
		super(Holder.direct(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 4,
				ArmorItem.Type.CHESTPLATE, 7,
				ArmorItem.Type.LEGGINGS, 6,
				ArmorItem.Type.BOOTS, 3
			),
			9,
			SoundEvents.ARMOR_EQUIP_IRON,
			() -> Ingredient.of(new ItemStack(CAItems.KNIGHT_CORPSE.get())),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "knight_iron"))),
			4.5f,
			0.33f
		)), type, properties.component(DataComponents.ATTRIBUTE_MODIFIERS,
			ItemAttributeModifiers.builder()
				.add(
					CAAttributes.GENERAL_DEFENSE,
					new AttributeModifier(
						ResourceLocation.fromNamespaceAndPath("caerula_arbor", "knight_iron_general_defense"),
						1.0,
						AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.ARMOR)
				.build()));
	}

    @Override
    public void setDamage(ItemStack stack, int damage){
   		int nowD = this.getDamage(stack);
   		int d = damage - nowD;
   		if (d>1) d = Math.max(d - 8, 1);
        super.setDamage(stack,Math.min(damage,nowD + d));
    }

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		if (itemstack.getItem() instanceof KnightIronItem iron){
			if (iron.getType() == ArmorItem.Type.HELMET)
				list.add(Component.translatable("item.caerula_arbor.knight_iron.poem_0"));
			if (iron.getType() == ArmorItem.Type.CHESTPLATE) 
				list.add(Component.translatable("item.caerula_arbor.knight_iron.poem_1"));
			if (iron.getType() == ArmorItem.Type.LEGGINGS) 
				list.add(Component.translatable("item.caerula_arbor.knight_iron.poem_2"));
			if (iron.getType() == ArmorItem.Type.BOOTS) 
				list.add(Component.translatable("item.caerula_arbor.knight_iron.poem_3"));
		}
		list.add(Component.translatable("item.caerula_arbor.knight_iron.desc"));
		list.add(Component.translatable("item.caerula_arbor.knight_iron.desc_1"));
	}

	private PlayState predicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.knight_armor.idle"));
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