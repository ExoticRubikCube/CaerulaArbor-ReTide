
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import com.apocalypse.caerulaarbor.init.CAAttributes;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;
import java.util.ArrayList;

public class MartusProtectionMobEffect extends MobEffect {
    public MartusProtectionMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -16777063);
        this.addAttributeModifier(Attributes.LUCK, "a1fe5c70-5eb0-326e-ab95-ca86390674e0", 1, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CAAttributes.GENERAL_DEFENSE.get(), "39a8533e-a27c-3ff8-b45b-150f72207a18", 9, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CAAttributes.MAGIC_RESISTANCE.get(), "8b082d27-ef84-3136-8123-1bd5642da4c4", 75, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(CAAttributes.MISSRATE.get(), "7ec16e5d-eb5c-3590-b108-74acf31d12e0", 25, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity == null)
            return;
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
                world.addParticle(CAParticleTypes.MARTUS_CHARS.get(), tx, (y + 1), yz, 0, 0.15, 0);
                world.addParticle(CAParticleTypes.MARTUS_CHARS.get(), tx, (y + 0.8), yz, 0, (-0.08), 0);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
