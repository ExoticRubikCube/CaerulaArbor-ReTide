
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.OceanIllusionEntity;
import com.susen36.caerulaarbor.client.model.entity.OceanIllusionModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanIllusionRenderer extends GeoEntityRenderer<OceanIllusionEntity> {
	public OceanIllusionRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanIllusionModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(OceanIllusionEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanIllusionEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(OceanIllusionEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
