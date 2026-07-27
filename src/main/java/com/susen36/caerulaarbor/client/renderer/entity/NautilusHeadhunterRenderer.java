
package com.susen36.caerulaarbor.client.renderer.entity;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.susen36.caerulaarbor.client.model.entity.NautilusHeadhunterModel;
import com.susen36.caerulaarbor.client.model.entity.layer.NautilusHeadhunterLayer;
import com.susen36.caerulaarbor.entity.NautilusHeadhunterEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class NautilusHeadhunterRenderer extends GeoEntityRenderer<NautilusHeadhunterEntity> {
	public NautilusHeadhunterRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NautilusHeadhunterModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new NautilusHeadhunterLayer(this));
	}

	@Override
	public RenderType getRenderType(NautilusHeadhunterEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, NautilusHeadhunterEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(NautilusHeadhunterEntity entityLivingBaseIn) {
		return 0.0F;
	}
}