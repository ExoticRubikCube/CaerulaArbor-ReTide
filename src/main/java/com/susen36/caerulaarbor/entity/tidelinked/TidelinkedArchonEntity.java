package com.susen36.caerulaarbor.entity.tidelinked;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TidelinkedArchonEntity extends AbstractTidelinkedEntity {

    public TidelinkedArchonEntity(Level world) {
        this(CAEntities.TIDELINKED_ARCHON.get(), world);
    }

    public TidelinkedArchonEntity(EntityType<TidelinkedArchonEntity> type, Level world) {
        super(type, world);
        this.bossInfo.setColor(ServerBossEvent.BossBarColor.YELLOW);
    }

    @Override
    protected String getAnimationPrefix() {
        return "animation.tidelinked_archon";
    }

    @Override
    protected int getRevivalDuration() {
        return 400;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.8, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && !TidelinkedArchonEntity.this.isFaking();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !TidelinkedArchonEntity.this.isFaking();
            }
        });
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Level world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity sourceentity = source.getEntity();
        if (sourceentity != null && this.isAlive() && this.getEntityData().get(DATA_SKILLP) <= 0 && !this.hasEffect(CAMobEffects.FAKE_DEATH) && this.distanceTo(sourceentity) <= 6) {
            Vec3 attackCenter = new Vec3((x + 1.8 * getLookAngle().x), (y + 1.5), (z + 1.8 * getLookAngle().z));
            List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, new AABB(attackCenter, attackCenter).inflate(5),
                    e -> e.getType().is(EntityUtils.SEA_BORN) && this.getTarget() == e
                        || !e.getType().is(EntityUtils.SEA_BORN) && (e instanceof Mob || e instanceof Player));
            int strongTargets = 0;
            for (LivingEntity entityiterator : targets) {
                if (entityiterator != this && entityiterator.getMaxHealth() >= 10) {
                    strongTargets++;
                }
            }
            if (strongTargets >= 2 || this.getHealth() < this.getMaxHealth() * 0.5) {
                this.setAnimation("animation.tidelinked_archon.enchantattack");
                this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()));
                CaerulaArbor.queueServerWork(12, () -> {
                    world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 2, 1);
                    for (LivingEntity entityiterator : targets) {
                        entityiterator.hurt(
                                CADamageTypes.source(world, CADamageTypes.REPELLER_ATTACK, this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.5));
                    }
                });
                this.getEntityData().set(DATA_DURATION, 53);
                this.getEntityData().set(DATA_SKILLP, 60);
            }
        }
        return super.hurt(source, amount);
    }

}