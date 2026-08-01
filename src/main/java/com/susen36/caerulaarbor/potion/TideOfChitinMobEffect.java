
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class TideOfChitinMobEffect extends MobEffect {
    public TideOfChitinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13382401);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "tide_of_chitin_attack_damage"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("caerulaarbor", "tide_of_chitin_max_health"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }


    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        {
            ItemStack setval = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY);
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.chitin_knife_selected = setval.copy();
            capability.syncPlayerVariables(entity);
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double perc;
        if (!(((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY)
                .getItem() == (ModCapabilities.getPlayerVariables(entity).chitin_knife_selected).getItem())) {
            if (world instanceof Level level) {
                if (level.isClientSide()) {
                    level.playLocalSound(x, y, z, SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, (float) 3.2, 1, false);
                }
            }
            perc = EntityUtils.getHealthPerc(entity);
            if ((Entity) entity instanceof LivingEntity livingEntity)
                livingEntity.removeEffect(CAMobEffects.TIDE_OF_CHITIN);
            if ((Entity) entity instanceof LivingEntity livingEntity)
                livingEntity.setHealth((float) (livingEntity.getMaxHealth() * perc));
        }
        if (ModCapabilities.getPlayerVariables(entity).kingShowPtc) {
            world.addParticle(CAParticles.KNIFEPTC.get(), (x + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.8)),
                    (z + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.1, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}