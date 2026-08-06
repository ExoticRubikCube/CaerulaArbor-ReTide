package com.susen36.caerulaarbor.entity.tidelinked;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

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

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder = builder.add(Attributes.MAX_HEALTH, 100);
        builder = builder.add(Attributes.ARMOR, 10);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 7);
        builder = builder.add(Attributes.ATTACK_SPEED, 5.2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.65);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 2000);
        return builder;
    }

}