
package com.apocalypse.caerulaarbor.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.apocalypse.caerulaarbor.client.model.OceanizedWardenisModel;
import com.apocalypse.caerulaarbor.client.model.layer.OceanizedWardenisLayer;
import com.apocalypse.caerulaarbor.entity.OceanizedWardenisEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class OceanizedWardenisRenderer extends GeoEntityRenderer<OceanizedWardenisEntity> {
	public OceanizedWardenisRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedWardenisModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedWardenisLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedWardenisEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedWardenisEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(OceanizedWardenisEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
