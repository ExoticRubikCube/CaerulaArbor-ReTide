package com.susen36.caerulaarbor.client.model.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCrawlerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PocketSeaCrawlerLayer extends GeoRenderLayer<PocketSeaCrawlerEntity> {
	private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/pocket_sea_crawlerfish_lit.png");

	public PocketSeaCrawlerLayer(GeoRenderer<PocketSeaCrawlerEntity> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(PoseStack poseStack, PocketSeaCrawlerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		RenderType glowRenderType = RenderType.eyes(LAYER);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
	}
}
