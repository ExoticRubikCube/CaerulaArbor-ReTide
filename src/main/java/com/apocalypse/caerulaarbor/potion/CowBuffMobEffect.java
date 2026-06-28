
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.entity.OceanizedCowEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.procedures.TrailReplaceProcedure;
import com.apocalypse.caerulaarbor.util.MathUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CowBuffMobEffect extends MobEffect {
    public CowBuffMobEffect() {
        super(MobEffectCategory.NEUTRAL, -1);
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
        if ((Entity) entity instanceof OceanizedCowEntity _datEntL0 && _datEntL0.getEntityData().get(OceanizedCowEntity.DATA_skill) && ((Entity) entity).isAlive()) {
            if (!((Entity) entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CaerulaArborModMobEffects.MUTE.get()))
                    && ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) <= ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
                if (WorldUtils.canGrief(world)) {
                    if (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x, y, z)) && !(world.getBlockFloorHeight(BlockPos.containing(x, y, z)) > 0)) {
                        TrailReplaceProcedure.execute(world, CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState(), (world.getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.WATER, x, y, z);
                    }
                    for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
                        if (CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))
                                && !(world.getBlockFloorHeight(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())) > 0)) {
                            if (Math.random() < 0.33) {
                                TrailReplaceProcedure.execute(world, CaerulaArborModBlocks.SEA_TRAIL_INIT.get().defaultBlockState(),
                                        (world.getFluidState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())).createLegacyBlock()).getBlock() == Blocks.WATER, x + directioniterator.getStepX(), y,
                                        z + directioniterator.getStepZ());
                            }
                        }
                    }
                    if ((Entity) entity instanceof OceanizedCowEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(OceanizedCowEntity.DATA_skill, false);
                    if ((Entity) entity instanceof OceanizedCowEntity animatable)
                        animatable.setTexture("oceanzied_cow_trailless");
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
