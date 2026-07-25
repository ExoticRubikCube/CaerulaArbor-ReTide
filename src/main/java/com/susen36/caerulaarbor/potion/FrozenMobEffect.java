
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.ArrayList;
import java.util.List;

public class FrozenMobEffect extends MobEffect {
    public FrozenMobEffect() {
        super(MobEffectCategory.HARMFUL, -3342337);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_movement_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_swim_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_attack_speed"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_jump_strength"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_knockback_resistance"), 0.5, AttributeModifier.Operation.ADD_VALUE);
        // TODO: NeoForge 1.21.1 removed NeoForgeMod.STEP_HEIGHT_ADDITION, reimplement when replacement is known
        // this.addAttributeModifier(NeoForgeMod.STEP_HEIGHT_ADDITION, "e8fabb9b-c39f-30f3-b4d4-73429435aea6", -1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_armor_toughness"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_armor"), -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "frozen_attack_damage"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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
            ((Entity) entity).hurt(entity.level().damageSources().freeze(), 1);
        }
        if (entity instanceof MagmaCube) {
            ((Entity) entity).hurt(entity.level().damageSources().freeze(), 1);
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