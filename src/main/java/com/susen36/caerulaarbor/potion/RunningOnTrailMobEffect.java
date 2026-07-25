
package com.susen36.caerulaarbor.potion;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RunningOnTrailMobEffect extends MobEffect {
    public RunningOnTrailMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -1);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "running_on_trail_movement_speed"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "running_on_trail_swim_speed"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.STEP_HEIGHT_ADDITION, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.STEP_HEIGHT_ADDITION, "325435aa-3381-3a76-975f-b75564d633af", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "running_on_trail_jump_strength"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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