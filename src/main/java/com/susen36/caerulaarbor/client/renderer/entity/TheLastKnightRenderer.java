
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.TheLastKnightModel;
import com.susen36.caerulaarbor.client.model.entity.layer.TheLastKnightLayer;
import com.susen36.caerulaarbor.entity.TheLastKnightEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TheLastKnightRenderer extends GeoEntityRenderer<TheLastKnightEntity> {
	public TheLastKnightRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TheLastKnightModel());
		this.addRenderLayer(new TheLastKnightLayer(this));
	}

	@Override
	public void preRender(PoseStack poseStack, TheLastKnightEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		this.shadowRadius = entity.getPhase() == 1 ? 1.2f : 1f;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(TheLastKnightEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
