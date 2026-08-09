package com.susen36.caerulaarbor.entity.shaper;

import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class RouteFractalEntity extends AbstractFractalEntity {
    public static final EntityDataAccessor<Integer> DATA_TIME_LEFT = SynchedEntityData.defineId(RouteFractalEntity.class, EntityDataSerializers.INT);

    public RouteFractalEntity(Level world) {
        this(CAEntities.ROUTE_FRACTAL.get(), world);
    }

    public RouteFractalEntity(EntityType<RouteFractalEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TIME_LEFT, 1800);
    }

    @Override
    protected EntityType<?> getSummonedFractalType() {
        return CAEntities.ROUTE_FRACTAL.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TimeLeft", this.entityData.get(DATA_TIME_LEFT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TimeLeft")) {
            this.entityData.set(DATA_TIME_LEFT, compound.getInt("TimeLeft"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        double timel;
        timel =  this.getEntityData().get(DATA_TIME_LEFT);
        if (timel <= 0) {
            this.hurt(this.damageSources().starve(), 10000);
        }
        this.getEntityData().set(DATA_TIME_LEFT, (int) (timel - 1));
        this.refreshDimensions();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
        builder = builder.add(Attributes.MAX_HEALTH, 80);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 6);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
        return builder;
    }

}