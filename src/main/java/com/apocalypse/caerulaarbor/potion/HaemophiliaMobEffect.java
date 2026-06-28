
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HaemophiliaMobEffect extends MobEffect {
    public HaemophiliaMobEffect() {
        super(MobEffectCategory.NEUTRAL, -3381505);
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
        double health_cur = 0;
        if (Math.round((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < Math.round((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
            health_cur = Math.max(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) * (0.975 - 0.025 * (double) amplifier), 0.5);
            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > 0.5 && ((Entity) entity).isAlive()) {
                if ((Entity) entity instanceof LivingEntity _entity)
                    _entity.setHealth((float) health_cur);
                for (int index0 = 0; index0 < 24; index0++) {
                    world.addParticle(CaerulaArborModParticleTypes.BLOODOOZE.get(), entity.getX(), (entity.getY() + 1.33), entity.getZ(), (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)), (Mth.nextDouble(RandomSource.create(), -0.05, 0.05)),
                            (Mth.nextDouble(RandomSource.create(), -1.25, 1.25)));
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 40);
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
