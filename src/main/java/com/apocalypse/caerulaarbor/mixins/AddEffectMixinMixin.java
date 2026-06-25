package com.apocalypse.caerulaarbor.mixins;

import org.spongepowered.asm.mixin.Mixin;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.MartusEntity;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = {LivingEntity.class}, priority = 65536)
public abstract class AddEffectMixinMixin {
    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true)
    public MobEffectInstance addShorterEffect(MobEffectInstance value){
        MobEffect effect = value.getEffect();
        if(effect.isInstantenous()) return value;
        if(effect.getCategory() == MobEffectCategory.HARMFUL){
            LivingEntity me = (LivingEntity) (Object) this;
            MobEffect resist = CaerulaArborModMobEffects.ESSENCE_RESISTANCE.get();
            if(!me.hasEffect(resist)) return value;
            MobEffectInstance resistInstance = me.getEffect(resist);
            int amplifier = 0;
            if (resistInstance != null) amplifier = resistInstance.getAmplifier();
            int newDuration = (int) (value.getDuration() * (1 - (amplifier + 1) * 0.25));
            me.removeEffect(resist);
            return new MobEffectInstance(
                    effect,
                    Math.max(1, newDuration),
                    amplifier,
                    value.isAmbient(),
                    value.isVisible(),
                    value.showIcon()
            );
        }
        return value;
    }

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    public void immortalSetHealth(float pHealth, CallbackInfo ci){
        LivingEntity me = (LivingEntity)(Object) this;
        if(me.hasEffect(CaerulaArborModMobEffects.IMMORTAL.get())){
            if(pHealth < 0.5) {
                ci.cancel();
                if (me.getMaxHealth() >= 0.5)
                	me.setHealth(0.5f);
                me.getPersistentData().putBoolean("immortalTriggered",true);
            }
        }
        if(me.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && !(me instanceof MartusEntity)){
            if(pHealth < me.getHealth()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void doNotDie(DamageSource pDamageSource, CallbackInfo ci){
        LivingEntity me = (LivingEntity)(Object) this;
        if(me.hasEffect(CaerulaArborModMobEffects.IMMORTAL.get())){
            ci.cancel();
            me.getPersistentData().putBoolean("immortalTriggered",true);
        }
        if(me.hasEffect(CaerulaArborModMobEffects.INVULNERABLE.get()) && !(me instanceof MartusEntity)){
            ci.cancel();
        }
    }
    
    @Shadow public abstract boolean addEffect(MobEffectInstance pEffectInstance, @org.jetbrains.annotations.Nullable Entity pEntity);
}
