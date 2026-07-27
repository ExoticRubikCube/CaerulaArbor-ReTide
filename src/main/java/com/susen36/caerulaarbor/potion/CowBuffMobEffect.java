
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.OceanizedCowEntity;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.CaerulaUtil;
import com.susen36.caerulaarbor.util.MathUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class CowBuffMobEffect extends MobEffect {
    public CowBuffMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity instanceof OceanizedCowEntity datEntL0 && datEntL0.getEntityData().get(OceanizedCowEntity.DATA_SKILL) && entity.isAlive()) {
            if (!(entity instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.MUTE))
                    && (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) <= ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                if (WorldUtils.canGrief(world)) {
                    if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z)) && !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) {
                        CaerulaUtil.replaceTrail(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), (world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER, x, y, z);
                    }
                    for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
                        if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))
                                && !(world.getBlockFloorHeight(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())) > 0)) {
                            if (Math.random() < 0.33) {
                                CaerulaUtil.replaceTrail(world, CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(),
                                        (world.getFluidState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())).createLegacyBlock()).getBlock() == Blocks.WATER, x + directioniterator.getStepX(), y,
                                        z + directioniterator.getStepZ());
                            }
                        }
                    }
                    if ((Entity) entity instanceof OceanizedCowEntity datEntSetL)
                        datEntSetL.getEntityData().set(OceanizedCowEntity.DATA_SKILL, false);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

}