
package com.susen36.caerulaarbor.potion;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.susen36.caerulaarbor.init.CAAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LessArmorMobEffect extends MobEffect {
    public LessArmorMobEffect() {
        super(MobEffectCategory.HARMFUL, -10079233);
        this.addAttributeModifier(Attributes.ARMOR, "b13a9de8-fdc7-3dc6-badd-339eef5041ec", -1, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "071e8fce-d067-355c-9bcd-431c8c87f34e", -1, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
