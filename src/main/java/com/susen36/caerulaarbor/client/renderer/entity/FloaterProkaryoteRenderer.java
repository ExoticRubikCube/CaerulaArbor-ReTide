
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.FloaterProkaryoteEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.FloaterProkaryoteLayer;
import com.susen36.caerulaarbor.client.model.entity.FloaterProkaryoteModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FloaterProkaryoteRenderer extends GeoEntityRenderer<FloaterProkaryoteEntity> {
	public FloaterProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FloaterProkaryoteModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new FloaterProkaryoteLayer(this));
	}

	@Override
	public RenderType getRenderType(FloaterProkaryoteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, FloaterProkaryoteEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 0.75f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(FloaterProkaryoteEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
