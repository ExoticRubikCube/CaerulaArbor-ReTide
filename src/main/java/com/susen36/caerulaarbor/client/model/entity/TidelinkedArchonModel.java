package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedArchonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TidelinkedArchonModel extends GeoModel<TidelinkedArchonEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/tidelinked_archon.png");

    @Override
    public ResourceLocation getAnimationResource(TidelinkedArchonEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/tidelinked_archon.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(TidelinkedArchonEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/tidelinked_archon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TidelinkedArchonEntity entity) {
        return TEXTURE;
    }

    @Override
    public void setCustomAnimations(TidelinkedArchonEntity animatable, long instanceId, AnimationState<TidelinkedArchonEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("Head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}