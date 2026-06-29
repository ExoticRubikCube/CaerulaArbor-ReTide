
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.apocalypse.caerulaarbor.util.MathUtils;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class PetReapMobEffect extends MobEffect {
    public PetReapMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -6710785);
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
        double angle;
        if ((Entity) entity instanceof Mob _mobEnt0 && _mobEnt0.isAggressive() && ((Entity) entity).isAlive()) {
            for (int index0 = 0; index0 < 120; index0++) {
                angle = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + 2.5 * Math.sin(angle)), y, (z + 2.5 * Math.cos(angle)), 8, 0.1, 0.1, 0.1, 0.2);
            }
            {
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof Monster) {
                        if (!(entityiterator == entity)) {
                            if (entityiterator instanceof LivingEntity target && ((Entity) entity) instanceof LivingEntity attacker) {
                                SIHelper.causeSanityInjury(target,
                                        attacker,
                                        (attacker.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 12,
                                        SanityEvent.Hurt.Type.ENTITY);
                            }
                        }
                    }
                    if (((Entity) entity instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == entityiterator) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0));
                    }
                }
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
