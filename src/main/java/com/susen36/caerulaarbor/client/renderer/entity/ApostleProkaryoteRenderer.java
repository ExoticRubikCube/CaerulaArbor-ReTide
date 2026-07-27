
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.ApostleProkaryoteModel;
import com.susen36.caerulaarbor.client.model.entity.layer.ApostleProkaryoteLayer;
import com.susen36.caerulaarbor.entity.ApostleProkaryoteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ApostleProkaryoteRenderer extends GeoEntityRenderer<ApostleProkaryoteEntity> {
	public ApostleProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ApostleProkaryoteModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new ApostleProkaryoteLayer(this));
	}

	@Override
	public RenderType getRenderType(ApostleProkaryoteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, ApostleProkaryoteEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 0.8f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(ApostleProkaryoteEntity entityLivingBaseIn) {
		return 0.0F;
	}
}