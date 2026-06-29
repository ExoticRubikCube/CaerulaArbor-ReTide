package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.RouteFractalModel;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteFractalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RouteFractalRenderer extends GeoEntityRenderer<RouteFractalEntity> {
	public RouteFractalRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new RouteFractalModel());
		this.shadowRadius = 0.6f;
	}

	@Override
	public RenderType getRenderType(RouteFractalEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, RouteFractalEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 0.75f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}

