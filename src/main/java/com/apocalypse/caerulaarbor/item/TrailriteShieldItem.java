package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
import java.util.UUID;
import java.util.function.Consumer;

public class TrailriteShieldItem extends ShieldItem {
    private static final UUID ADD_ARMOR_UUID = UUID.fromString("d8a06f80-7b2c-4e8a-9b8c-1234567890ab");
    private static final UUID ADD_DEFENSE_UUID = UUID.fromString("e9b17991-8c3d-5f9b-0c9d-0987654321ba");
    private final AttributeModifier addArmor = new AttributeModifier(ADD_ARMOR_UUID, "trailrite_shield", 5.0, AttributeModifier.Operation.ADDITION);
    private final AttributeModifier addDefense = new AttributeModifier(ADD_DEFENSE_UUID, "trailrite_shield", 0.75, AttributeModifier.Operation.MULTIPLY_BASE);

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
    public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, level, list, flag);
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
        if (v >= max - 1) {
            super.setDamage(stack, max - 1);
        } else {
            super.setDamage(stack, v);
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return 0;
        }
        return 1;
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
            AttributeInstance defenseInstance = living.getAttribute(CAAttributes.GENERAL_DEFENSE.get());
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
    public boolean canBeHurtBy(DamageSource pDamageSource) {
        return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
    }
}