
package com.apocalypse.caerulaarbor.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.apocalypse.caerulaarbor.client.model.TideDeathrepellerModel;
import com.apocalypse.caerulaarbor.entity.TideDeathrepellerEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class TideDeathrepellerRenderer extends GeoEntityRenderer<TideDeathrepellerEntity> {
	public TideDeathrepellerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TideDeathrepellerModel());
		this.shadowRadius = 0.8f;
	}

	@Override
	public RenderType getRenderType(TideDeathrepellerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, TideDeathrepellerEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1.2f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(TideDeathrepellerEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
