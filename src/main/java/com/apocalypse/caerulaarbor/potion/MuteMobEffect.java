
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class MuteMobEffect extends MobEffect {
	public MuteMobEffect() {
		super(MobEffectCategory.HARMFUL, -11904668);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<>();
		cures.add(new ItemStack(Items.MILK_BUCKET));
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
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
    }

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return MathUtils.isMultipleOf(duration, 10);
	}
}
