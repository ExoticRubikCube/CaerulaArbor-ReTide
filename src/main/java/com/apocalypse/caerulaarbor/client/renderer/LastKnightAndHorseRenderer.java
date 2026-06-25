
package com.apocalypse.caerulaarbor.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.apocalypse.caerulaarbor.client.model.LastKnightAndHorseModel;
import com.apocalypse.caerulaarbor.client.model.layer.LastKnightAndHorseLayer;
import com.apocalypse.caerulaarbor.entity.LastKnightAndHorseEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class LastKnightAndHorseRenderer extends GeoEntityRenderer<LastKnightAndHorseEntity> {
	public LastKnightAndHorseRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new LastKnightAndHorseModel());
		this.shadowRadius = 1.2f;
		this.addRenderLayer(new LastKnightAndHorseLayer(this));
	}

	@Override
	public RenderType getRenderType(LastKnightAndHorseEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, LastKnightAndHorseEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(LastKnightAndHorseEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
