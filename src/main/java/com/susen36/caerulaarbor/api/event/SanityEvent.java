package com.susen36.caerulaarbor.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
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

    public static class Hurt extends SanityEvent implements ICancellableEvent {
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

    public static class Heal extends SanityEvent implements ICancellableEvent {
        public Heal(LivingEntity victim, double amount) {
            super(victim, amount);
        }
    }

    public static class Break extends SanityEvent implements ICancellableEvent {
        public Break(LivingEntity victim) {
            super(victim, 0);
        }
    }
}