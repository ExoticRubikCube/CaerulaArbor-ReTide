
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

public class AddReachMobEffect extends MobEffect {
    public AddReachMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10066432);
        this.addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "e1ecf318-fb03-3b5c-8f72-f0c607ec5341", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "afdcee44-7b74-36b1-ba4f-23bd6a1cf564", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
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
