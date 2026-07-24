
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.AccumulatorCloneModel;
import com.susen36.caerulaarbor.client.model.entity.layer.AccumulatorCloneLayer;
import com.susen36.caerulaarbor.entity.AccumulatorCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AccumulatorCloneRenderer extends GeoEntityRenderer<AccumulatorCloneEntity> {
	public AccumulatorCloneRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AccumulatorCloneModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new AccumulatorCloneLayer(this));
	}

	@Override
	public RenderType getRenderType(AccumulatorCloneEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, AccumulatorCloneEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
