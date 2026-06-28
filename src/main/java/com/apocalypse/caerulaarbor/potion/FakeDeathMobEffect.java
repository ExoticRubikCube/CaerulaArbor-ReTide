
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.entity.TideBishopEntity;
import com.apocalypse.caerulaarbor.entity.TideDeathrepellerEntity;
import com.apocalypse.caerulaarbor.utils.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FakeDeathMobEffect extends MobEffect {
    public FakeDeathMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13596966);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "e385d18c-726e-31f5-8320-7deb56d84071", 10, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "af1372ae-1e1c-3d92-8c22-163353b62809", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "1549abeb-c898-38c1-b87d-cdb8866505bd", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "d2ad47ed-30d5-3421-a371-7eb8d9b95037", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        if (entity == null)
            return;
        if ((Entity) entity instanceof LivingEntity _entity)
            _entity.setHealth(1);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        if ((Entity) entity instanceof LivingEntity _entity)
            _entity.setHealth((float) (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.025 * ((double) amplifier + 1)));
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        if (entity == null)
            return;
        if (entity instanceof TideBishopEntity) {
            ((TideBishopEntity) entity).setAnimation("animation.tidebishop.die_idle");
        }
        if (entity instanceof TideDeathrepellerEntity) {
            ((TideDeathrepellerEntity) entity).setAnimation("animation.deathrepeller.die_idle");
        }
        entity.setShiftKeyDown(false);
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
