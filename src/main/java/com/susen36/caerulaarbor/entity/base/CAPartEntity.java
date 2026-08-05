package com.susen36.caerulaarbor.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class CAPartEntity<T extends LivingEntity> extends PartEntity<T> {
    public final T parentMob;
    public final String name;
    private final EntityDimensions size;
    private boolean canPickable = true;

    public CAPartEntity(T parentMob, String name, float width, float height) {
        super(parentMob);
        this.parentMob = parentMob;
        this.name = name;
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    public CAPartEntity(T parentMob, String name,boolean canPickable, float width, float height) {
        this(parentMob,name,width,height);
        this.canPickable = canPickable;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    public boolean isPickable() {
        return canPickable;
    }

    @Nullable
    public ItemStack getPickResult() {
        if(this.isPickable()) {
            return ItemStack.EMPTY;
        }
        return this.parentMob.getPickResult();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return this.canPickable && !this.isInvulnerableTo(source) && parentHurt(source, amount);
    }

    protected abstract boolean parentHurt(@NotNull DamageSource source, float amount);

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
