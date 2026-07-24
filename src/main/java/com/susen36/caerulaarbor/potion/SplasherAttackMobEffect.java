
package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.entity.bullets.FishSplashEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class SplasherAttackMobEffect extends MobEffect {
    public SplasherAttackMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13421773);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double num;
        double rand;
        double dama;
        num = 0;
        if ((Entity) entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.TRAIL_BUFF.get())) {
            dama = ((Entity) entity instanceof LivingEntity livingEntity1 && livingEntity1.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity1.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.5;
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 48), (y + 6), (z + 48), (x - 48), (y - 6), (z - 48)))) {
                if ((entityiterator instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) < 5) {
                    continue;
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    continue;
                }
                if (entityiterator instanceof Player && (entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
                    continue;
                }
                rand = Mth.nextDouble(RandomSource.create(), 7, 11);
                if (entityiterator instanceof LivingEntity livEnt6 && livEnt6.hasEffect(CAMobEffects.TRAIL_BUFF.get())) {
                    if (world instanceof ServerLevel projectileLevel) {
                        Projectile entityToSpawn = new Object() {
                            public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                                AbstractArrow entityToSpawn = new FishSplashEntity(CAEntities.FISH_SPLASH.get(), level);
                                entityToSpawn.setOwner(shooter);
                                entityToSpawn.setBaseDamage(damage);
                                entityToSpawn.setKnockback(knockback);
                                entityToSpawn.setSilent(true);
                                entityToSpawn.setCritArrow(true);
                                return entityToSpawn;
                            }
                        }.getArrow(projectileLevel, (Entity) entity, (float) dama, 0);
                        entityToSpawn.setPos((entityiterator.getX()), (entityiterator.getY() + rand), (entityiterator.getZ()));
                        entityToSpawn.shoot(0, (-1), 0, (float) 1.5, 0);
                        projectileLevel.addFreshEntity(entityToSpawn);
                    }
                    num = num + 1;
                    new Object() {
                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                            if (world instanceof ServerLevel level)
                                level.sendParticles(CAParticles.SEA_SPLASH.get(), (entityiterator.getX() + ((x - entityiterator.getX()) / 40) * timedloopiterator),
                                        (entityiterator.getY() + 9 + ((y - (entityiterator.getY() + 9)) / 40) * timedloopiterator), (entityiterator.getZ() + ((z - entityiterator.getZ()) / 40) * timedloopiterator), 1, 0.1, 0.1, 0.1, 0.01);
                            final int tick2 = ticks;
                            CaerulaArborMod.queueServerWork(tick2, () -> {
                                if (timedlooptotal > timedloopiterator + 1) {
                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                }
                            });
                        }
                    }.timedLoop(0, 40, 1);
                }
                if (num >= 3) {
                    break;
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 40);
    }
}
