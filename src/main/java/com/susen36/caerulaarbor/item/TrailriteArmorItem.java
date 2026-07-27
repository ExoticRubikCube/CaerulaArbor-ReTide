
package com.susen36.caerulaarbor.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


public class TrailriteArmorItem extends ArmorItem implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public TrailriteArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial(
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
		), type, properties);
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

	@Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        String name = "caerula_arbor_attribute_modifier";
        UUID uuid = new UUID(slot.toString().hashCode(), 0);
        if (slot == this.getEquipmentSlot()){
            map = HashMultimap.create(map);
            map.put(CAAttributes.SANITY_RESISTANCE,
                    new AttributeModifier(uuid, name , 17.5f, AttributeModifier.Operation.ADDITION));
            map.put(CAAttributes.SANITY_RATE.get(),
                    new AttributeModifier(uuid, name , 0.75f, AttributeModifier.Operation.ADDITION));
            map.put(CAAttributes.GENERAL_DEFENSE,
                    new AttributeModifier(uuid, name , 3.5f, AttributeModifier.Operation.ADDITION));
            map.put(CAAttributes.MAGIC_RESISTANCE,
                    new AttributeModifier(uuid, name , 8f, AttributeModifier.Operation.ADDITION));
        }
        return map;
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