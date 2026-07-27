
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.ReaperFishEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.ReaperFishLayer;
import com.susen36.caerulaarbor.client.model.entity.ReaperFishModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ReaperFishRenderer extends GeoEntityRenderer<ReaperFishEntity> {
	public ReaperFishRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ReaperFishModel());
		this.shadowRadius = 0.8f;
		this.addRenderLayer(new ReaperFishLayer(this));
	}

	@Override
	public RenderType getRenderType(ReaperFishEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, ReaperFishEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 2.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(ReaperFishEntity entityLivingBaseIn) {
		return 0.0F;
	}
}