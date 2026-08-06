package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.TidelinkedArchonModel;
import com.susen36.caerulaarbor.entity.tidelinked.TidelinkedArchonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TidelinkedArchonRenderer extends GeoEntityRenderer<TidelinkedArchonEntity> {
    public TidelinkedArchonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TidelinkedArchonModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public RenderType getRenderType(TidelinkedArchonEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, TidelinkedArchonEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float scale = 1.2f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    protected float getDeathMaxRotation(TidelinkedArchonEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
