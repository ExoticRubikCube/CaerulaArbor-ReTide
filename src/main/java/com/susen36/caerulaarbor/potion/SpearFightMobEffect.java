
package com.susen36.caerulaarbor.potion;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class SpearFightMobEffect extends MobEffect {
    public SpearFightMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3355444);
        this.addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "9f09e814-768f-30ef-8126-99947a514b97", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "5b1909df-711e-3b48-ac79-71f39809f0be", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
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
