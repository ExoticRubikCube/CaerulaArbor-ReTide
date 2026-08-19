
package com.susen36.caerulaarbor.client.model.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class OceanizedWardenisLayer extends GeoRenderLayer<OceanizedWardenisEntity> {
	private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/warden/wardenis_lit.png");

	public OceanizedWardenisLayer(GeoRenderer<OceanizedWardenisEntity> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(PoseStack poseStack, OceanizedWardenisEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		float tendril = animatable.getTendrilAnimation(partialTick);
		float heart = animatable.getHeartAnimation(partialTick);
		float intensity = Math.max(tendril, heart);
		if (intensity <= 0.0F) {
			return;
		}
		int a = Math.max(4, (int) (intensity * 255.0F));
		int color = 0x00FFFFFF | (a << 24);
		RenderType glowRenderType = RenderType.eyes(LAYER);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, color);
	}
}
