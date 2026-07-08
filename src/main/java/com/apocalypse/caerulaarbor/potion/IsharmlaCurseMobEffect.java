package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class IsharmlaCurseMobEffect extends MobEffect {
    public IsharmlaCurseMobEffect() {
        super(MobEffectCategory.HARMFUL, -11580593);
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
        if (entity == null)
            return;
        double d;
        double t;
        d = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.25 * ((double) amplifier + 1);
        ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.ISHARMLA_CURSED), (float) d);
        if (world instanceof ServerLevel level)
            level.sendParticles(CAParticles.ISHARMLA_CURSE_PARTICLE.get(), x, (y + 0.8), z, 32, 0.8, 0.8, 0.8, 0.1);
        if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ISHARMLA_TEAR_HURT_0.get(), SoundSource.HOSTILE, 2, 1);
        }
        t = Mth.nextDouble(RandomSource.create(), 0, 6.283);
        if ((Entity) entity instanceof Mob mob)
            mob.getNavigation().moveTo((x + 2 * Math.sin(t)), y, (z + 2 * Math.cos(t)), 1.5);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }
}
