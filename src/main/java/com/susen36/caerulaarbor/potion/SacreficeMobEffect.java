
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SacreficeMobEffect extends MobEffect {
    public SacreficeMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3407872);
        this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "sacrefice_max_health"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).kingShowPtc) {
            if ((double) amplifier < 1) {
                world.addParticle(CAParticles.ARCHFIEND_KEEP.get(), (x + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.6)),
                        (z + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.05, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
            } else {
                world.addParticle(CAParticles.ARCHFIEND_RESEV.get(), (x + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.6)),
                        (z + Mth.nextDouble(RandomSource.create(), -0.55, 0.55)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.05, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
            }
        }
        return true;
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