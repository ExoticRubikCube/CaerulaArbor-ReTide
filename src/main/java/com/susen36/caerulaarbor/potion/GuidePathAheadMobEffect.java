
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.GuideAbyssalEntity;
import com.susen36.caerulaarbor.entity.OceanizedHorseEntity;
import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.MathUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class GuidePathAheadMobEffect extends MobEffect {
    public GuidePathAheadMobEffect() {
        super(MobEffectCategory.NEUTRAL, -13395457);
        this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "guide_path_ahead_armor"), 12, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "guide_path_ahead_armor_toughness"), 9, AttributeModifier.Operation.ADD_VALUE);
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
        if (entity == null)
             return true;
        BlockState target;
        if (((Entity) entity).isAlive()) {
            if (WorldUtils.canGrief(world) && ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                if (!((Entity) entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.MUTE.get()))) {
                    target = (world.getBlockState(BlockPos.containing(x, y, z)));
                    if (((Entity) entity instanceof GuideAbyssalEntity datEntI ? datEntI.getEntityData().get(GuideAbyssalEntity.DATA_LAYLIMIT) : 0) > 0) {
                        if ((target.canBeReplaced() || !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) && !(target.getBlock() == CABlocks.SEA_TRAIL_GROWN.get())
                                && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) >= 0) {
                            if (CABlocks.SEA_TRAIL_GROWN.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(pos, false);
                                }
                                BlockState placedState = CABlocks.SEA_TRAIL_GROWN.get().defaultBlockState();
                                if (placedState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                                    placedState = placedState.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(BlockPos.containing(x, y, z)).getType() == Fluids.WATER);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), placedState, 3);
                                if (world instanceof Level level) {
                                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_STEP, SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof GuideAbyssalEntity datEntSetI)
                                    datEntSetI.getEntityData().set(GuideAbyssalEntity.DATA_LAYLIMIT, ((Entity) entity instanceof GuideAbyssalEntity datEntI ? datEntI.getEntityData().get(GuideAbyssalEntity.DATA_LAYLIMIT) : 0) - 1);
                            }
                        }
                    } else if (((Entity) entity instanceof OceanizedHorseEntity datEntI ? datEntI.getEntityData().get(OceanizedHorseEntity.DATA_LAY_LIMIT) : 0) > 0) {
                        if (target.canBeReplaced()) {
                            if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(pos, false);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), 3);
                                if (world instanceof Level level) {
                                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_STEP, SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof OceanizedHorseEntity datEntSetI)
                                    datEntSetI.getEntityData().set(OceanizedHorseEntity.DATA_LAY_LIMIT, ((Entity) entity instanceof OceanizedHorseEntity datEntI ? datEntI.getEntityData().get(OceanizedHorseEntity.DATA_LAY_LIMIT) : 0) - 1);
                            }
                        }
                    }
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