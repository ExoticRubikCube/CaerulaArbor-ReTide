package com.susen36.caerulaarbor.api.event;

import net.minecraft.world.entity.LivingEntity;

import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SanityEvent extends LivingEvent {
    private double amount;

    public SanityEvent(LivingEntity victim, double amount) {
        super(victim);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Cancelable
    public static class Hurt extends SanityEvent {
        private final Type type;
        private final LivingEntity source;

        public Hurt(LivingEntity source, LivingEntity victim, double amount, Type type) {
            super(victim, amount);
            this.type = type;
            this.source = source;
        }

        public LivingEntity getSource() {
            return source;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            BLOCK, ENTITY, POTION, FOOD, DEFAULT
        }
    }

    @Cancelable
    public static class Heal extends SanityEvent {
        public Heal(LivingEntity victim, double amount) {
            super(victim, amount);
        }
    }

    @Cancelable
    public static class Break extends SanityEvent {
        public Break(LivingEntity victim) {
            super(victim, 0);
        }
    }
}