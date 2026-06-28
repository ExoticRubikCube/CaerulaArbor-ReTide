
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.apocalypse.caerulaarbor.util.MathUtils;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class PowerOfAnchorMobEffect extends MobEffect {
    public PowerOfAnchorMobEffect() {
        super(MobEffectCategory.NEUTRAL, -6684724);
        this.addAttributeModifier(CaerulaArborModAttributes.SANITY_MODIFIER.get(), "fca8c1c6-9152-3107-9573-bd52fa24d2f9", -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        if (entity instanceof Player) {
            ModCapabilities.getSanityInjury(entity).heal(10);
            ModCapabilities.getPlayerVariables(entity).player_light = Math.min(ModCapabilities.getPlayerVariables(entity).player_light + 0.125, 100.0);
            ModCapabilities.getPlayerVariables(entity).syncPlayerVariables(entity);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
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
