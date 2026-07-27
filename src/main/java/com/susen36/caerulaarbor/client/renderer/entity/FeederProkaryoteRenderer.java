
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.FeederProkaryoteEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.FeederProkaryoteLayer;
import com.susen36.caerulaarbor.client.model.entity.FeederProkaryoteModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FeederProkaryoteRenderer extends GeoEntityRenderer<FeederProkaryoteEntity> {
	public FeederProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FeederProkaryoteModel());
		this.shadowRadius = 0.8f;
		this.addRenderLayer(new FeederProkaryoteLayer(this));
	}

	@Override
	public RenderType getRenderType(FeederProkaryoteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, FeederProkaryoteEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(FeederProkaryoteEntity entityLivingBaseIn) {
		return 0.0F;
	}
}