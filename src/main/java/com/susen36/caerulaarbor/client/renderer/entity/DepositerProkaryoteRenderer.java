
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.DepositerProkaryoteModel;
import com.susen36.caerulaarbor.client.model.entity.layer.DepositerProkaryoteLayer;
import com.susen36.caerulaarbor.entity.DepositerProkaryoteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DepositerProkaryoteRenderer extends GeoEntityRenderer<DepositerProkaryoteEntity> {
	public DepositerProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DepositerProkaryoteModel());
		this.shadowRadius = 0.625f;
		this.addRenderLayer(new DepositerProkaryoteLayer(this));
	}

	@Override
	public RenderType getRenderType(DepositerProkaryoteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, DepositerProkaryoteEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(DepositerProkaryoteEntity entityLivingBaseIn) {
		return 0.0F;
	}
}