
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForgeMod;

public class FrozenMobEffect extends MobEffect {
    public FrozenMobEffect() {
        super(MobEffectCategory.HARMFUL, -3342337);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_swim_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_attack_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_jump_strength"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_knockback_resistance"), 0.5, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.STEP_HEIGHT, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_step_height"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_armor_toughness"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_armor"), -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_attack_damage"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double dh;
        double dw;
        if (entity instanceof Creeper) {
            CompoundTag dataIndex1 = new CompoundTag();
            entity.saveWithoutId(dataIndex1);
            dataIndex1.putBoolean("ignited", false);
            entity.load(dataIndex1);
            if ((Entity) entity instanceof Creeper creeper)
                creeper.setSwellDir(0);
        }
        if (entity instanceof Blaze) {
            entity.hurt(entity.level().damageSources().freeze(), 1);
        }
        if (entity instanceof MagmaCube) {
            entity.hurt(entity.level().damageSources().freeze(), 1);
        }
        dh = entity.getBbHeight() * 0.5;
        dw = entity.getBbWidth() * 0.5;
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.SNOWFLAKE, x, (y + dh), z, 8, dw, dh, dw, 0.05);
        if (world instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.ITEM_SNOWBALL, x, (y + dh), z, 2, dw, dh, dw, 0.05);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }
}