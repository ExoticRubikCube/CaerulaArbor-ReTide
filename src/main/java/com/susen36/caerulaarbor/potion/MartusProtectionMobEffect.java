
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class MartusProtectionMobEffect extends MobEffect {
    public MartusProtectionMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16777063);
        this.addAttributeModifier(Attributes.LUCK, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "martus_protection_luck"), 1, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), ResourceLocation.fromNamespaceAndPath("caerulaarbor", "martus_protection_general_defense"), 9, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(CAAttributes.MAGIC_RESISTANCE.get(), ResourceLocation.fromNamespaceAndPath("caerulaarbor", "martus_protection_magic_resistance"), 75, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(CAAttributes.MISSRATE.get(), ResourceLocation.fromNamespaceAndPath("caerulaarbor", "martus_protection_missrate"), 25, AttributeModifier.Operation.ADD_VALUE);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity.tickCount % 5 == 0) {
            double y = entity.getY();
            double r;
            double t;
            double tx;
            double yz;
            double randint;
            randint = Mth.nextInt(RandomSource.create(), 0, 39);
            for (int index0 = 0; index0 < 40; index0++) {
                t = randint + index0 * 9;
                r = 1.5 + 0.5 * Math.sin(Math.toRadians(index0 * 36));
                tx = entity.getX() + r * Math.sin(Math.toRadians(t));
                yz = entity.getZ() + r * Math.cos(Math.toRadians(t));
                world.addParticle(CAParticles.MARTUS_CHARS.get(), tx, (y + 1), yz, 0, 0.15, 0);
                world.addParticle(CAParticles.MARTUS_CHARS.get(), tx, (y + 0.8), yz, 0, (-0.08), 0);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}