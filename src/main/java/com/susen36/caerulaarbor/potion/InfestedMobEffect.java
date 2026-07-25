package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class InfestedMobEffect extends MobEffect {
    public InfestedMobEffect() {
        super(MobEffectCategory.HARMFUL, -3407668);
    }

    // TODO: 1.21.1 removed MobEffect.getCurativeItems(), curative logic needs migration to ConsumeEffect
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        if (entity == null)
             return true;
        double dam;
        if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization < 3) {
            dam = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * Mth.nextDouble(RandomSource.create(), 0.1, 0.25) * ((double) amplifier + 1);
            if ((Entity) entity instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.POWER_OF_ANCHOR.get())) {
                dam = dam * 0.1;
            }
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.OCEANIZE_DAMAGE), (float) dam);
            if (!((Entity) entity instanceof LivingEntity livEnt5 && livEnt5.hasEffect(CAMobEffects.POWER_OF_ANCHOR.get()))) {
                if (Math.random() < 0.33) {
                    dam = Mth.nextInt(RandomSource.create(), 0, 7);
                    if (dam == 0) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, (int) (double) amplifier));
                    } else if (dam == 1) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 160, (int) (double) amplifier));
                    } else if (dam == 2) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, (int) (double) amplifier));
                    } else if (dam == 3) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, (int) (double) amplifier));
                    } else if (dam == 4) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, (int) (double) amplifier));
                    } else if (dam == 5) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
                    } else if (dam == 6) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), 160, 0));
                    } else if (dam == 7) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 160, 0));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity == null)
            return;
        double ampli;
        if (entity instanceof Player) {
            ampli = amplifier;
            if ((double) amplifier > 2) {
                ampli = 2;
            }
            {
                double setval = ampli + 1;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_oceanization = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            SIHelper.causeSanityInjury(entity, 750 * ((double) amplifier + 1), SanityEvent.Hurt.Type.POTION);
            if ((Entity) entity instanceof ServerPlayer player) {
                Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "they_shall_welcome"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }
            if ((double) amplifier >= 2) {
                if ((Entity) entity instanceof ServerPlayer player) {
                    Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "they_shall_pay"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
                {
                    double setval = 0;
                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.disoclusion = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 2, 1);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 40);
    }
}