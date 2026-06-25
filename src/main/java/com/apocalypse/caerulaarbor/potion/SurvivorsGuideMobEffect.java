
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

public class SurvivorsGuideMobEffect extends MobEffect {
    public SurvivorsGuideMobEffect() {
        super(MobEffectCategory.NEUTRAL, -3407872);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "253a73d0-8589-3a25-8b5c-32b3035cce6e", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR, "1e800216-1635-33d1-a232-0f265561ee39", 1, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CaerulaArborModAttributes.GENERAL_DEFENSE.get(), "616325a4-4df9-3089-a919-ffcea08ee504", 0.25, AttributeModifier.Operation.ADDITION);
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
