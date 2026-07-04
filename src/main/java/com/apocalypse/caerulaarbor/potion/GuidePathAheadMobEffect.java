
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.entity.GuideAbyssalEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.MathUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
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
        this.addAttributeModifier(Attributes.ARMOR, "6d4b81d9-bed2-396c-918f-891f0ef22012", 12, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "97fa89c4-3837-3dcb-a05f-db0233b6a457", 9, AttributeModifier.Operation.ADDITION);
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
        BlockState target;
        if (((Entity) entity).isAlive()) {
            if (WorldUtils.canGrief(world) && ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                if (!((Entity) entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CAMobEffects.MUTE.get()))) {
                    target = (world.getBlockState(BlockPos.containing(x, y, z)));
                    if (((Entity) entity instanceof GuideAbyssalEntity _datEntI ? _datEntI.getEntityData().get(GuideAbyssalEntity.DATA_LAYLIMIT) : 0) > 0) {
                        if ((target.canBeReplaced() || !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) && !(target.getBlock() == CABlocks.SEA_TRAIL_GROWN.get())
                                && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) >= 0) {
                            if (CABlocks.SEA_TRAIL_GROWN.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(_pos, false);
                                }
                                BlockState placedState = CABlocks.SEA_TRAIL_GROWN.get().defaultBlockState();
                                if (placedState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                                    placedState = placedState.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(BlockPos.containing(x, y, z)).getType() == Fluids.WATER);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), placedState, 3);
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_STEP, SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof GuideAbyssalEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(GuideAbyssalEntity.DATA_LAYLIMIT, ((Entity) entity instanceof GuideAbyssalEntity _datEntI ? _datEntI.getEntityData().get(GuideAbyssalEntity.DATA_LAYLIMIT) : 0) - 1);
                            }
                        }
                    } else if (((Entity) entity instanceof OceanizedHorseEntity _datEntI ? _datEntI.getEntityData().get(OceanizedHorseEntity.DATA_LAY_LIMIT) : 0) > 0) {
                        if (target.canBeReplaced()) {
                            if (CABlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(_pos, false);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), CABlocks.SEA_TRAIL_INIT.get().defaultBlockState(), 3);
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SCULK_VEIN_STEP, SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof OceanizedHorseEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(OceanizedHorseEntity.DATA_LAY_LIMIT, ((Entity) entity instanceof OceanizedHorseEntity _datEntI ? _datEntI.getEntityData().get(OceanizedHorseEntity.DATA_LAY_LIMIT) : 0) - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }
}
