
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.entity.GuideAbyssalEntity;
import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.MathUtils;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

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
        BlockState target = Blocks.AIR.defaultBlockState();
        if (((Entity) entity).isAlive()) {
            if (WorldUtils.canGrief(world) && ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                if (!((Entity) entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.MUTE.get()))) {
                    target = (world.getBlockState(BlockPos.containing(x, y, z)));
                    if (((Entity) entity instanceof GuideAbyssalEntity _datEntI ? _datEntI.getEntityData().get(GuideAbyssalEntity.DATA_laylimit) : 0) > 0) {
                        if ((target.canBeReplaced() || !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) && !(target.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWN.get())
                                && target.getDestroySpeed(world, BlockPos.containing(0, 0, 0)) >= 0) {
                            if (CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(_pos, false);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.SEA_TRAIL_GROWN.get().defaultBlockState(), 3);
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.step")), SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof GuideAbyssalEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(GuideAbyssalEntity.DATA_laylimit, ((Entity) entity instanceof GuideAbyssalEntity _datEntI ? _datEntI.getEntityData().get(GuideAbyssalEntity.DATA_laylimit) : 0) - 1);
                            }
                        }
                    } else if (((Entity) entity instanceof OceanizedHorseEntity _datEntI ? _datEntI.getEntityData().get(OceanizedHorseEntity.DATA_lay_limit) : 0) > 0) {
                        if (target.canBeReplaced()) {
                            if (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z))) {
                                {
                                    BlockPos _pos = BlockPos.containing(x, y, z);
                                    Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
                                    world.destroyBlock(_pos, false);
                                }
                                world.setBlock(BlockPos.containing(x, y, z), CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState(), 3);
                                if (world instanceof Level _level) {
                                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_vein.step")), SoundSource.NEUTRAL, 2, 1);
                                }
                                if ((Entity) entity instanceof OceanizedHorseEntity _datEntSetI)
                                    _datEntSetI.getEntityData().set(OceanizedHorseEntity.DATA_lay_limit, ((Entity) entity instanceof OceanizedHorseEntity _datEntI ? _datEntI.getEntityData().get(OceanizedHorseEntity.DATA_lay_limit) : 0) - 1);
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
