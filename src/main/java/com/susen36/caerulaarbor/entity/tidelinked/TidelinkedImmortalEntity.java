package com.susen36.caerulaarbor.entity.tidelinked;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TidelinkedImmortalEntity extends AbstractTidelinkedEntity {

    public TidelinkedImmortalEntity(Level world) {
        this(CAEntities.TIDELINKED_IMMORTAL.get(), world);
    }

    public TidelinkedImmortalEntity(EntityType<TidelinkedImmortalEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected String getAnimationPrefix() {
        return "animation.tidelinked_immortal";
    }

    @Override
    protected int getRevivalDuration() {
        return 200;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.8, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && !TidelinkedImmortalEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !TidelinkedImmortalEntity.this.isFaking();
            }

            @Override
            protected void resetAttackCooldown() {
                this.ticksUntilNextAttack = this.adjustedTickDelay(10);
            }

            @Override
            protected int getAttackInterval() {
                return this.adjustedTickDelay(10);
            }

        });
    }
    @Override
    public void baseTick() {
        super.baseTick();
        double skillCooldown = this.getEntityData().get(DATA_SKILLP);
        if (skillCooldown <= 0 && this.getHealth() < this.getMaxHealth() * 0.5) {
            LivingEntity target = this.getTarget();
            if (target != null && this.distanceTo(target) <= 4) {
                this.setAnimation("animation.tidelinked_immortal.combo");
                this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY(), target.getZ()));
                CaerulaArborMod.queueServerWork(17, () -> {
                    if (this.isAlive()) {
                        this.repellerChop(target, 0.75F);
                    }
                });
                CaerulaArborMod.queueServerWork(23, () -> {
                    if (this.isAlive()) {
                        this.repellerChop(target, 0.75F);
                    }
                });
                CaerulaArborMod.queueServerWork(35, () -> {
                    if (this.isAlive()) {
                        this.repellerChop(target, 0.75F);
                    }
                });
                CaerulaArborMod.queueServerWork(42, () -> {
                    if (this.isAlive()) {
                        this.repellerChop(target, 1.25F);
                    }
                });
                this.getEntityData().set(DATA_DURATION, 53);
                this.getEntityData().set(DATA_SKILLP, 300);
            }
        }
    }

    public void repellerChop(LivingEntity target,float rate) {
        if (this.isWithinMeleeAttackRange(target) && this.getSensing().hasLineOfSight(target)) {
            float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE)* rate;
            DamageSource damagesource = this.damageSources().mobAttack(this);
            boolean flag = target.hurt(damagesource, f);
            if (flag) {
                float f1 = this.getKnockback(target, damagesource);
                if (f1 > 0.0F && target instanceof LivingEntity) {
                    target.knockback(f1 * 0.5F, Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, (double)1.0F, 0.6));
                }

                Level level = this.level();
                if (level instanceof ServerLevel serverlevel1) {
                    EnchantmentHelper.doPostAttackEffects(serverlevel1, target, damagesource);
                }

                this.heal(3);
                this.setLastHurtMob(target);
                this.playAttackSound();
            }

        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 75);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 8);
        builder = builder.add(Attributes.ATTACK_SPEED, 5.2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.65);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        return builder;
    }

}