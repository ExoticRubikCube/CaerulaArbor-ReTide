package com.susen36.caerulaarbor.client.model.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.TheLastKnightEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TheLastKnightLayer extends GeoRenderLayer<TheLastKnightEntity> {
	private static final ResourceLocation LAYER_KNIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/last_knight_lit_0.png");
	private static final ResourceLocation LAYER_HORSE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/last_knight_lit.png");

	public TheLastKnightLayer(GeoRenderer<TheLastKnightEntity> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(PoseStack poseStack, TheLastKnightEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		ResourceLocation layer = animatable.getPhase() == 1 ? LAYER_HORSE : LAYER_KNIGHT;
		RenderType glowRenderType = RenderType.eyes(layer);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
	}
}
