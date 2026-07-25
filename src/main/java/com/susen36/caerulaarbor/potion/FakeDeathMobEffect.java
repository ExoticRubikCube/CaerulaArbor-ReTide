
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.TideBishopEntity;
import com.susen36.caerulaarbor.entity.TideDeathrepellerEntity;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FakeDeathMobEffect extends MobEffect {
    public FakeDeathMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13596966);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_knockback_resistance"), 10, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_attack_damage"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.ENTITY_REACH, "d2ad47ed-30d5-3421-a371-7eb8d9b95037", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
            livingEntity.setHealth(1);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
            livingEntity.setHealth((float) (livingEntity.getHealth() + livingEntity.getMaxHealth() * 0.025 * ((double) amplifier + 1)));
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        if (entity instanceof TideBishopEntity tideBishop) {
            tideBishop.setAnimation("animation.tidebishop.die_idle");
        }
        if (entity instanceof TideDeathrepellerEntity deathrepellerEntity) {
            deathrepellerEntity.setAnimation("animation.deathrepeller.die_idle");
        }
        entity.setShiftKeyDown(false);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
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