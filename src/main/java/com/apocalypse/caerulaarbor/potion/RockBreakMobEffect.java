
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class RockBreakMobEffect extends MobEffect {
    public RockBreakMobEffect() {
        super(MobEffectCategory.HARMFUL, -6710887);
        this.addAttributeModifier(Attributes.ARMOR, "643337bb-099c-3b1a-9484-1ee3bf459028", -0.35, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "fb973f54-7fd2-375c-8a38-fec9a7e3aee1", -0.35, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "5027b477-4a8a-3f94-95e2-b83b020b5cc4", -0.35, AttributeModifier.Operation.MULTIPLY_BASE);
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
