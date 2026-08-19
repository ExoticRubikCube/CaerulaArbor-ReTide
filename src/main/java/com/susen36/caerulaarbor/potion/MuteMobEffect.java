
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.LevelAccessor;

public class MuteMobEffect extends MobEffect {
	public MuteMobEffect() {
		super(MobEffectCategory.HARMFUL, -11904668);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (world instanceof ServerLevel level)
            level.sendParticles(CAParticles.MUTENESS.get(), entity.getX(), entity.getY(), entity.getZ(), 2, 1, 1, 1, 0.1);
        if (entity instanceof Creeper) {
            CompoundTag dataIndex2 = new CompoundTag();
            entity.saveWithoutId(dataIndex2);
            dataIndex2.putBoolean("ignited", false);
            entity.load(dataIndex2);
            if ((Entity) entity instanceof Creeper creeper)
                creeper.setSwellDir(0);
        }
	    return true;
    }

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (double) duration % (double) 10 == 0;
	}
}