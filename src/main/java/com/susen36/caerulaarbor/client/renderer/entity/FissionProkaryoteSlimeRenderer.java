package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.FissionProkaryoteSlimeModel;
import com.susen36.caerulaarbor.client.model.entity.layer.FissionProkaryoteSlimeLayer;
import com.susen36.caerulaarbor.entity.slime.FissionProkaryoteSlimeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FissionProkaryoteSlimeRenderer extends GeoEntityRenderer<FissionProkaryoteSlimeEntity> {
	public FissionProkaryoteSlimeRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FissionProkaryoteSlimeModel());
		this.shadowRadius = 0f;
		this.addRenderLayer(new FissionProkaryoteSlimeLayer(this));
	}

	@Override
	public RenderType getRenderType(FissionProkaryoteSlimeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, FissionProkaryoteSlimeEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = (float) entity.getSlimeSize();
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(FissionProkaryoteSlimeEntity entityLivingBaseIn) {
		return 0.0F;
	}
}