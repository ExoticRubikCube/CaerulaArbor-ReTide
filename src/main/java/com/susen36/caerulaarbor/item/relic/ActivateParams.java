package com.susen36.caerulaarbor.item.relic;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Relic 物品通用右键激活参数。
 * 与 RelicItemBase.performActivate() 配合使用，把声音/粒子/写入值这些"变化项"集中配置，
 * 让 SimpleRelicItem 及自定义类可以用一行 builder() 表达差异化需求。
 */
public final class ActivateParams {

    public enum ActivateMode {
        /** {@code CARelics.XXX.get().get(player) < 0}，用于 HAND_ENGRAVE、SURVIVOR_CONTRACT 等数值型遗物。*/
        BELOW_ZERO,
        /** {@code !CARelics.XXX.get().gained(player)}，用于普通 Boolean 型（0=未拥有,1=已拥有）。*/
        NOT_GAINED
    }

    private final ActivateMode mode;
    private final int setValue;
    private final SoundEvent soundEvent;
    private final float volume;
    private final float pitch;
    private final ParticleOptions particle;
    private final int particleCount;
    private final double particleYOffset;
    private final double particleSpeed;
    private final boolean showActivationOverlay;
    private final boolean shrinkAfterUse;

    private ActivateParams(ActivateMode mode, int setValue, SoundEvent soundEvent, float volume, float pitch,
                          ParticleOptions particle, int particleCount, double particleYOffset, double particleSpeed,
                          boolean showActivationOverlay, boolean shrinkAfterUse) {
        this.mode = mode;
        this.setValue = setValue;
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
        this.particle = particle;
        this.particleCount = particleCount;
        this.particleYOffset = particleYOffset;
        this.particleSpeed = particleSpeed;
        this.showActivationOverlay = showActivationOverlay;
        this.shrinkAfterUse = shrinkAfterUse;
    }

    public ActivateMode mode() { return mode; }
    public int setValue() { return setValue; }
    public SoundEvent soundEvent() { return soundEvent; }
    public float volume() { return volume; }
    public float pitch() { return pitch; }
    public ParticleOptions particle() { return particle; }
    public int paticleCount() { return particleCount; }
    public double particleYOffset() { return particleYOffset; }
    public double particleSpeed() { return particleSpeed; }
    public boolean showActivationOverlay() { return showActivationOverlay; }
    public boolean shrinkAfterUse() { return shrinkAfterUse; }

    public static Builder builder() {
        return new Builder();
    }

    /** 最常用：Boolean 型未拥有 → 设为 1。*/
    public static ActivateParams standardBoolean() {
        return builder().build();
    }

    /** 最常用：Numeric 型未激活（<0）→ 设为 0。*/
    public static ActivateParams standardNumericNeg1() {
        return builder()
                .mode(ActivateMode.BELOW_ZERO)
                .setValue(0)
                .build();
    }

    public static final class Builder {
        private ActivateMode mode = ActivateMode.NOT_GAINED;
        private int setValue = 1;
        private SoundEvent soundEvent = SoundEvents.PLAYER_LEVELUP;
        private float volume = 2F;
        private float pitch = 1F;
        private ParticleOptions particle = ParticleTypes.HAPPY_VILLAGER;
        private int particleCount = 72;
        private double particleYOffset = 0D;
        private double particleSpeed = 1D;
        private boolean showActivationOverlay = true;
        private boolean shrinkAfterUse = false;

        public Builder mode(ActivateMode mode) { this.mode = mode; return this; }
        public Builder setValue(int v) { this.setValue = v; return this; }
        public Builder sound(SoundEvent e, float v, float p) { this.soundEvent = e; this.volume = v; this.pitch = p; return this; }
        public Builder particle(ParticleOptions p, int count) { this.particle = p; this.particleCount = count; return this; }
        public Builder particleYOffset(double offset) { this.particleYOffset = offset; return this; }
        public Builder particleSpeed(double s) { this.particleSpeed = s; return this; }
        public Builder showOverlay(boolean b) { this.showActivationOverlay = b; return this; }
        public Builder shrink(boolean b) { this.shrinkAfterUse = b; return this; }

        public ActivateParams build() {
            return new ActivateParams(mode, setValue, soundEvent, volume, pitch, particle,
                    particleCount, particleYOffset, particleSpeed, showActivationOverlay, shrinkAfterUse);
        }
    }
}