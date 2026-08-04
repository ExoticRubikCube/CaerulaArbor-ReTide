package com.susen36.caerulaarbor.entity.enderdragon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OceanizedEnderDragonPart  extends PartEntity<OceanizedEnderDragonEntity> {
    public final OceanizedEnderDragonEntity parentMob;
    public final String name;
    private final EntityDimensions size;

    public OceanizedEnderDragonPart(OceanizedEnderDragonEntity parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parentMob;
        this.name = name;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(this, source, amount);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        throw new UnsupportedOperationException();
    }

    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    public boolean shouldBeSaved() {
        return false;
    }
}
