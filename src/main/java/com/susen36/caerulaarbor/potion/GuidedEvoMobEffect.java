
package com.susen36.caerulaarbor.potion;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuidedEvoMobEffect extends MobEffect {
    public GuidedEvoMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -5532226);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false));
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
