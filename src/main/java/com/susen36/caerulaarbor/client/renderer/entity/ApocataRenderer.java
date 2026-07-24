
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.ApocataModel;
import com.susen36.caerulaarbor.entity.ApocataEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ApocataRenderer extends GeoEntityRenderer<ApocataEntity> {
	public ApocataRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ApocataModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(ApocataEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, ApocataEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(ApocataEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
