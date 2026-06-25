
package com.apocalypse.caerulaarbor.potion;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class IzumikLearnMobEffect extends MobEffect {
    public IzumikLearnMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13071);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5f69212e-9a33-3b80-b50a-f95638380bd1", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(CaerulaArborModAttributes.GENERAL_DEFENSE.get(), "c12bfa0e-843d-3ac9-b2d8-47fd4cf397e3", 0.5, AttributeModifier.Operation.ADDITION);
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
