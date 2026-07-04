package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class InfestedMobEffect extends MobEffect {
    public InfestedMobEffect() {
        super(MobEffectCategory.HARMFUL, -3407668);
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
        double dam;
        if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization < 3) {
            dam = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * Mth.nextDouble(RandomSource.create(), 0.1, 0.25) * ((double) amplifier + 1);
            if ((Entity) entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CAMobEffects.POWER_OF_ANCHOR.get())) {
                dam = dam * 0.1;
            }
            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanize_damage")))), (float) dam);
            if (!((Entity) entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CAMobEffects.POWER_OF_ANCHOR.get()))) {
                if (Math.random() < 0.33) {
                    dam = Mth.nextInt(RandomSource.create(), 0, 7);
                    if (dam == 0) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, (int) (double) amplifier));
                    } else if (dam == 1) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.POISON, 160, (int) (double) amplifier));
                    } else if (dam == 2) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, (int) (double) amplifier));
                    } else if (dam == 3) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, (int) (double) amplifier));
                    } else if (dam == 4) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, (int) (double) amplifier));
                    } else if (dam == 5) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
                    } else if (dam == 6) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.FROZEN.get(), 160, 0));
                    } else if (dam == 7) {
                        if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 160, 0));
                    }
                }
            }
        }
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
                double _setval = ampli + 1;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.player_oceanization = _setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            SIHelper.causeSanityInjury(entity, 750 * ((double) amplifier + 1), SanityEvent.Hurt.Type.POTION);
            if ((Entity) entity instanceof ServerPlayer _player) {
                Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "they_shall_welcome"));
                AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                if (!_ap.isDone()) {
                    for (String criteria : _ap.getRemainingCriteria())
                        _player.getAdvancements().award(_adv, criteria);
                }
            }
            if ((double) amplifier >= 2) {
                if ((Entity) entity instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "they_shall_pay"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
                {
                    double _setval = 0;
                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.disoclusion = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
            }
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 2, 1);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 40);
    }
}
