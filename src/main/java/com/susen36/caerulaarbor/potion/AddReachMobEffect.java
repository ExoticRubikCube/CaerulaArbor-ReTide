
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

public class AddReachMobEffect extends MobEffect {
    public AddReachMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10066432);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.BLOCK_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.BLOCK_REACH, "e1ecf318-fb03-3b5c-8f72-f0c607ec5341", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.ENTITY_REACH, "afdcee44-7b74-36b1-ba4f-23bd6a1cf564", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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