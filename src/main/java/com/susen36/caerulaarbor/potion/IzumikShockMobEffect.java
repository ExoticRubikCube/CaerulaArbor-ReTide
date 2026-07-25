package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.entity.IzumikEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class IzumikShockMobEffect extends MobEffect {
    public IzumikShockMobEffect() {
        super(MobEffectCategory.HARMFUL, -45858);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "izumik_shock_attack_damage"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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
        LivingEntity izumik;
        double dama;
        if (entity.isAlive()) {
            izumik = world.getEntitiesOfClass(IzumikEntity.class, AABB.ofSize(new Vec3(x, y, z), 27, 27, 27), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            dama = 12;
            if (izumik != null) {
                dama = izumik.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? izumik.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
            }
            entity.hurt(CADamageTypes.source(world, CADamageTypes.IZUMIK_SKILL), (float) (dama * 0.12));
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
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
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