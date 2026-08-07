package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.PocketSeaCrawlerModel;
import com.susen36.caerulaarbor.client.model.entity.layer.PocketSeaCrawlerLayer;
import com.susen36.caerulaarbor.client.model.entity.layer.PocketSeaCrawlerPowerLayer;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCrawlerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PocketSeaCrawlerRenderer extends GeoEntityRenderer<PocketSeaCrawlerEntity> {
	public PocketSeaCrawlerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PocketSeaCrawlerModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new PocketSeaCrawlerLayer(this));
		this.addRenderLayer(new PocketSeaCrawlerPowerLayer(this));
	}

	@Override
	public RenderType getRenderType(PocketSeaCrawlerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, PocketSeaCrawlerEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		this.scaleWidth = 1.75f;
		this.scaleHeight = 1.75f;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}