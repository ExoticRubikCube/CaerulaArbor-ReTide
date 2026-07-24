
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
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

public class EngravedTriumphMobEffect extends MobEffect {
    public EngravedTriumphMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16751002);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "ad3ee760-5e55-36db-8e49-63dc3b67a750", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "1feedda3-ba88-3e2a-8856-5212332ac8b2", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "814b3564-fb66-3420-ba9e-9b9c027d3843", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(CAAttributes.MAGIC_RESISTANCE.get(), "10c5f399-c9c7-303e-a104-ba1499e7a8bc", 0.25, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "d0105d7e-b5da-3757-bed1-8bec5afeb872", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
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
