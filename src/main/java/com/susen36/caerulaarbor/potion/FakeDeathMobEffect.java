
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedBishopEntity;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedImmortalEntity;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FakeDeathMobEffect extends MobEffect {
    public FakeDeathMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13596966);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_knockback_resistance"), 10, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "fake_death_attack_damage"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.ENTITY_REACH, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.ENTITY_REACH, "d2ad47ed-30d5-3421-a371-7eb8d9b95037", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
            livingEntity.setHealth(1);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
            livingEntity.setHealth((float) (livingEntity.getHealth() + livingEntity.getMaxHealth() * 0.025 * ((double) amplifier + 1)));
        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        if (entity instanceof TidelinkedBishopEntity tidelinkedBishop) {
            tidelinkedBishop.setAnimation("animation.tidelinked_bishop.die_idle");
        }
        if (entity instanceof TidelinkedImmortalEntity tidelinkedImmortalEntity) {
            tidelinkedImmortalEntity.setAnimation("animation.tidelinked_immortal.die_idle");
        }
        entity.setShiftKeyDown(false);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

}