package com.susen36.caerulaarbor.entity.enderdragon;

import com.susen36.caerulaarbor.entity.base.CAPartEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class OceanizedEnderDragonPart  extends CAPartEntity<OceanizedEnderDragonEntity> {

    public OceanizedEnderDragonPart(OceanizedEnderDragonEntity parentMob, String name, float width, float height) {
        super(parentMob, name, width, height);
    }

    public OceanizedEnderDragonPart(OceanizedEnderDragonEntity parentMob, String name,boolean canPickable, float width, float height) {
        super(parentMob,name,canPickable,width,height);
    }

    @Override
    protected boolean parentHurt(@NotNull DamageSource source, float amount) {
        return this.parentMob.hurt(this, source, amount);
    }
}
