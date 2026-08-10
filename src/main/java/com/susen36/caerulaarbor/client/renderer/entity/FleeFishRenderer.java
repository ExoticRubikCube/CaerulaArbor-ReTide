
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.FleeFishModel;
import com.susen36.caerulaarbor.client.model.entity.layer.FleeFishLayer;
import com.susen36.caerulaarbor.entity.FleeFishEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FleeFishRenderer extends GeoEntityRenderer<FleeFishEntity> {
	public FleeFishRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FleeFishModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new FleeFishLayer(this));
	}

	@Override
	public void preRender(PoseStack poseStack, FleeFishEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1.2f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(FleeFishEntity entityLivingBaseIn) {
		return 0.0F;
	}
}