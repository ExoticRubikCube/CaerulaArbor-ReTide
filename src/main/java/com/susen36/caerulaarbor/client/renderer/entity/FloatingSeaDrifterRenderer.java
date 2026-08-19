
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.FloatingSeaDrifterModel;
import com.susen36.caerulaarbor.entity.FloatingSeaDrifterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FloatingSeaDrifterRenderer extends GeoEntityRenderer<FloatingSeaDrifterEntity> {
	public FloatingSeaDrifterRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FloatingSeaDrifterModel());
		this.shadowRadius = 0.3f;
	}

	@Override
	public void preRender(PoseStack poseStack, FloatingSeaDrifterEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}