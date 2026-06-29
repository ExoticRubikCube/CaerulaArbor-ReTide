
package com.apocalypse.caerulaarbor.potion;

import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.apocalypse.caerulaarbor.init.CAAttributes;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class EndspeaerBrandguideBuffMobEffect extends MobEffect {
    public EndspeaerBrandguideBuffMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16763956);
        this.addAttributeModifier(Attributes.ARMOR, "7b0edc10-9817-37cf-89ee-2effc354738e", 4, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "8ff8a8b9-1316-3766-ad85-31afa0062e80", 2, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "53665cb1-9b07-3d88-b2b7-a3a4878cee0f", 4, AttributeModifier.Operation.MULTIPLY_BASE);
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
