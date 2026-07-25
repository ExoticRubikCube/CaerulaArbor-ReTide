
package com.susen36.caerulaarbor.potion;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpearFightMobEffect extends MobEffect {
    public SpearFightMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3355444);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.BLOCK_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.BLOCK_REACH, "9f09e814-768f-30ef-8126-99947a514b97", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.ENTITY_REACH, "5b1909df-711e-3b48-ac79-71f39809f0be", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
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