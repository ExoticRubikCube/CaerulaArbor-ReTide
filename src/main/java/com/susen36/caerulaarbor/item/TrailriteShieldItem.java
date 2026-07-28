package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;


public class TrailriteShieldItem extends ShieldItem {
    private final AttributeModifier addArmor = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_shield_armor"), 5.0, AttributeModifier.Operation.ADD_VALUE);
    private final AttributeModifier addDefense = new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "trailrite_shield_defense"), 0.75, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public TrailriteShieldItem() {
        super(new Item.Properties().durability(16384).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return Ingredient.of(new ItemStack(CAItems.TRAILRITE.get())).test(repairitem);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
        ItemStack retval = new ItemStack(this);
        retval.setDamageValue(itemstack.getDamageValue() + 1);
        if (retval.getDamageValue() >= retval.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return retval;
    }

    @Override
    public boolean isRepairable(ItemStack itemstack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_0"));
        list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description"));
        list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_pre"));
        if (Screen.hasShiftDown()) {
            list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_1"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_2"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_3"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_4"));
            list.add(Component.translatable("item.caerula_arbor.trailrite_shield.description_5"));
        } else {
            list.add(Component.translatable("item.caerula_arbor.trailrite_armor.description_1"));
        }
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int max = this.getMaxDamage(stack);
        int v = Math.min(damage, this.getDamage(stack) + 1);
        super.setDamage(stack, Math.min(v, max - 1));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1 ? 0 : 1;
    }

    private boolean shouldFunc(ItemStack stack) {
        return this.getDamage(stack) < this.getMaxDamage(stack) - 1;
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (!this.shouldFunc(itemstack)) {
            return;
        }
        if (entity instanceof LivingEntity living) {
            AttributeInstance defenseInstance = living.getAttribute(CAAttributes.GENERAL_DEFENSE);
            AttributeInstance armorInstance = living.getAttribute(Attributes.ARMOR);
            if (defenseInstance != null && armorInstance != null) {
                defenseInstance.removeModifier(this.addDefense);
                armorInstance.removeModifier(this.addArmor);
                if (selected) {
                    armorInstance.addTransientModifier(this.addArmor);
                }
                if (living.getUseItem().is(this)) {
                    defenseInstance.addTransientModifier(this.addDefense);
                }
            }
        }
    }

    @Override
    public boolean canBeHurtBy(ItemStack stack, DamageSource pDamageSource) {
        return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
    }
}