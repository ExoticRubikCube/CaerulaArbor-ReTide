package com.susen36.caerulaarbor.entity.isharmla;

import com.susen36.caerulaarbor.entity.base.CAPartEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class IsharmlaPart extends CAPartEntity<IsharmlaEntity> {
    public IsharmlaPart(IsharmlaEntity parentMob, String name, float width, float height) {
        super(parentMob, name, width, height);
    }

    public IsharmlaPart(IsharmlaEntity parentMob, String name, boolean canPickable, float width, float height) {
        super(parentMob, name, canPickable, width, height);
    }

    @Override
    public boolean isPickable() {
        return this.parentMob.isMonster() && super.isPickable();
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        if (this.parentMob.isMonster()) {
            return ItemStack.EMPTY;
        }
        return super.getPickResult();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return this.parentMob.isMonster() && super.hurt(source, amount);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return this.parentMob.isMonster() ? super.getDimensions(pose) : EntityDimensions.fixed(0.0F, 0.0F);
    }

    @Override
    public boolean isInvisible() {
        return !this.parentMob.isMonster() || super.isInvisible();
    }

    @Override
    protected boolean parentHurt(@NotNull DamageSource source, float amount) {
        return this.parentMob.hurt(this, source, amount);
    }
}