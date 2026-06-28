package com.apocalypse.caerulaarbor.client.model.entity.layer;

import com.apocalypse.caerulaarbor.entity.SkadiCorruptedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SkadiCorruptedLayer extends GeoRenderLayer<SkadiCorruptedEntity> {
	private static final ResourceLocation LAYER = new ResourceLocation(
		"caerula_arbor", "textures/entities/skadi_corrupted_lit_0.png");
	private static final ResourceLocation LAYER_1 = new ResourceLocation(
		"caerula_arbor", "textures/entities/skadi_corrupted_lit_1.png");
	private static final ResourceLocation LAYER_2 = new ResourceLocation(
		"caerula_arbor", "textures/entities/skadi_corrupted_lit.png");


	public SkadiCorruptedLayer(GeoRenderer<SkadiCorruptedEntity> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(PoseStack poseStack, SkadiCorruptedEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		RenderType glowRenderType = RenderType.eyes(LAYER);
		if(animatable.getPhase() == 1) glowRenderType = RenderType.eyes(LAYER_1);
		else if(animatable.getPhase() == 2) glowRenderType = RenderType.eyes(LAYER_2);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
	}
}
