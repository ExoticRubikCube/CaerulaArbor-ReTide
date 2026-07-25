
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EndspeaerBrandguideBuffMobEffect extends MobEffect {
    public EndspeaerBrandguideBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16763956);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_armor"), 4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_armor_toughness"), 2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), ResourceLocation.fromNamespaceAndPath("caerulaarbor", "endspeaer_brandguide_buff_general_defense"), 4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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