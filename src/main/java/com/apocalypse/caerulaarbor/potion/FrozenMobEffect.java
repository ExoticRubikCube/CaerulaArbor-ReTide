
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class FrozenMobEffect extends MobEffect {
    public FrozenMobEffect() {
        super(MobEffectCategory.HARMFUL, -3342337);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "8922b6c1-6d48-33e3-b722-38c69d2e5222", -1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "ea07d8a3-7c54-31cf-a97b-b360a7c7bdfd", -1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "51edf12a-1385-3193-a3e9-4fc6f18b9bcd", -1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, "044bbf44-aa84-3132-8b2a-5674186ae8b9", -1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7867da7d-0b51-3f2f-8b33-476555413ccf", 0.5, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), "e8fabb9b-c39f-30f3-b4d4-73429435aea6", -1, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "19abee04-cf6c-39cc-bf1c-ba6c38b014f4", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, "5795c748-fd3e-3c4e-a4e8-041dd19beb60", -0.4, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "da8f9207-9bee-3e06-aae9-3019b2ec862a", -0.5, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
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
            if ((Entity) entity instanceof Creeper _creeper)
                _creeper.setSwellDir(0);
        }
        if (entity instanceof Blaze) {
            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FREEZE)), 1);
        }
        if (entity instanceof MagmaCube) {
            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FREEZE)), 1);
        }
        dh = entity.getBbHeight() * 0.5;
        dw = entity.getBbWidth() * 0.5;
        if (world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.SNOWFLAKE, x, (y + dh), z, 8, dw, dh, dw, 0.05);
        if (world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.ITEM_SNOWBALL, x, (y + dh), z, 2, dw, dh, dw, 0.05);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }
}
