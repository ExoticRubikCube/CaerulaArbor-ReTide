package com.susen36.caerulaarbor.potion;

import com.susen36.babel.init.BabelMobEffects;
import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.advancements.AdvancementHolder;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class InfestedMobEffect extends MobEffect {
    public InfestedMobEffect() {
        super(MobEffectCategory.HARMFUL, -3407668);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double dam;
        if ((ModCapabilities.getPlayerVariables(entity)).player_oceanization < 3) {
            dam = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * Mth.nextDouble(RandomSource.create(), 0.1, 0.25) * ((double) amplifier + 1);
            if ((Entity) entity instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.POWER_OF_ANCHOR)) {
                dam = dam * 0.1;
            }
            entity.hurt(CADamageTypes.source(world, CADamageTypes.OCEANIZE_DAMAGE), (float) dam);
            if (!((Entity) entity instanceof LivingEntity livEnt5 && livEnt5.hasEffect(CAMobEffects.POWER_OF_ANCHOR))) {
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
                            livingEntity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN, 160, 0));
                    } else if (dam == 7) {
                        if ((Entity) entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                            livingEntity.addEffect(new MobEffectInstance(BabelMobEffects.STUN, 160, 0));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double ampli;
        if (entity instanceof Player) {
            ampli = amplifier;
            if ((double) amplifier > 2) {
                ampli = 2;
            }
            {
                int setval = (int) (ampli + 1);
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                    capability.player_oceanization = setval;
                    capability.syncPlayerVariables(entity);
            }
            EPUtils.causeSanityInjury(entity, 37.5 * ((double) amplifier + 1));
            if ((Entity) entity instanceof ServerPlayer player) {
                AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "they_shall_welcome"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }
            if ((double) amplifier >= 2) {
                if ((Entity) entity instanceof ServerPlayer player) {
                    AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "they_shall_pay"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
                {
                    double setval = 0;
                    PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                        capability.disoclusion = setval;
                        capability.syncPlayerVariables(entity);
                }
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 2, 1);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return (double) duration % (double) 40 == 0;
    }
}