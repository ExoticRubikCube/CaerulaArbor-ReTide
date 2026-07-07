package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.entity.IzumikEntity;
import com.apocalypse.caerulaarbor.init.CAAttributes;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class IzumikShockMobEffect extends MobEffect {
    public IzumikShockMobEffect() {
        super(MobEffectCategory.HARMFUL, -45858);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "39923994-e8ca-3482-a472-83b3fbefd6ae", -0.5, AttributeModifier.Operation.MULTIPLY_BASE);
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
        LivingEntity izumik;
        double dama;
        if (((Entity) entity).isAlive()) {
            izumik = world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(x, y, z), 27, 27, 27), e -> true).stream().sorted(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).findFirst().orElse(null);
            dama = 12;
            if (izumik != null) {
                dama = izumik.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? izumik.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
            }
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.IZUMIK_SKILL), (float) (dama * 0.12));
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.END_ROD, x, (y + 0.75), z, 24, 0.75, 0.75, 0.75, 0.1);
            if (Math.random() < 0.33) {
                if ((Entity) entity instanceof LivingEntity livingEntity8 && livingEntity8.getAttributes().hasAttribute(CAAttributes.NUMB.get()))
                    livingEntity8.getAttribute(CAAttributes.NUMB.get()).setBaseValue(
                            (((Entity) entity instanceof LivingEntity livingEntity7 && livingEntity7.getAttributes().hasAttribute(CAAttributes.NUMB.get()) ? livingEntity7.getAttribute(CAAttributes.NUMB.get()).getBaseValue() : 0)
                                    + 1));
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.FIREWORK, x, (y + 0.75), z, 24, 0.75, 0.75, 0.75, 0.1);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 20);
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
