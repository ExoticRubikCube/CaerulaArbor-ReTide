package com.apocalypse.caerulaarbor.entity.routeshaper;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class RouteFractalEntity extends AbstractFractalEntity {
    public static final EntityDataAccessor<Integer> DATA_time_left = SynchedEntityData.defineId(RouteFractalEntity.class, EntityDataSerializers.INT);

    public RouteFractalEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(CAEntities.ROUTE_FRACTAL.get(), world);
    }

    public RouteFractalEntity(EntityType<RouteFractalEntity> type, Level world) {
        super(type, world);
        xpReward = 8;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_time_left, 1800);
    }

    @Override
    protected EntityType<?> getSummonedFractalType() {
        return CAEntities.ROUTE_FRACTAL.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Datatime_left", this.entityData.get(DATA_time_left));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Datatime_left"))
            this.entityData.set(DATA_time_left, compound.getInt("Datatime_left"));
    }

    @Override
    public void baseTick() {
        super.baseTick();
        double timel;
        timel =  this.getEntityData().get(DATA_time_left);
        if (timel <= 0) {
            this.hurt(new DamageSource((this.level()).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.STARVE)), 10000);
        }
        this.getEntityData().set(DATA_time_left, (int) (timel - 1));
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

