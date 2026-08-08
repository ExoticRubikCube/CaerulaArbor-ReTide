package com.susen36.caerulaarbor.potion;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.IzumikOffspringEntity;
import com.susen36.caerulaarbor.entity.MartusEntity;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.MathUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

public class AngerOfTideMobEffect extends MobEffect {
    public AngerOfTideMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10092544);
    }

    

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double minDist;
        Entity enemy = null;
        Entity curEnemy;
        minDist = 999;
        if (entity instanceof IzumikOffspringEntity) {
             return true;
        }
        curEnemy = (Entity) entity instanceof Mob mobEnt ? mobEnt.getTarget() : null;
        if (!(curEnemy == null) && curEnemy.isAlive()) {
             return true;
        }
        if (!(entity instanceof MartusEntity)) {
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 12), (z + 32), (x - 32), (y - 9), (z - 32)))) {
                if (entity.isInWater() ^ entityiterator.isInWater()) {
                    continue;
                }
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_born")))) {
                    continue;
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "sea_friend")))) {
                    continue;
                }
                if (entityiterator instanceof Player) {
                    continue;
                }
                if (entityiterator instanceof LivingEntity livEnt11 && livEnt11.isBaby()) {
                    continue;
                }
                if ((Entity) entity instanceof LivingEntity livEnt12 && livEnt12.getType().is(EntityTypeTags.UNDEAD) && entityiterator instanceof LivingEntity livEnt13 && livEnt13.getType().is(EntityTypeTags.UNDEAD)) {
                    continue;
                }
                if (entityiterator.getPersistentData().getBoolean("seabornForgive")) {
                    continue;
                }
                if (EntityUtils.isSameTeam(entity, entityiterator)) {
                    continue;
                }
                if (entity.distanceTo(entityiterator) < minDist) {
                    minDist = entity.distanceTo(entityiterator);
                    enemy = entityiterator;
                }
            }
            if (enemy instanceof LivingEntity ent && entity instanceof Mob mob)
                mob.setTarget(ent);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

}