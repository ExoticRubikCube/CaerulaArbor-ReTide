package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.CaerulaOffspringModel;
import com.susen36.caerulaarbor.client.model.entity.layer.CaerulaOffspringLayer;
import com.susen36.caerulaarbor.entity.CaerulaOffspringEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CaerulaOffspringRenderer extends GeoEntityRenderer<CaerulaOffspringEntity> {
	public CaerulaOffspringRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new CaerulaOffspringModel());
		this.shadowRadius = 0.4f;
		this.addRenderLayer(new CaerulaOffspringLayer(this));
	}

	@Override
	public RenderType getRenderType(CaerulaOffspringEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, CaerulaOffspringEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1.25f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(CaerulaOffspringEntity entityLivingBaseIn) {
		return 0.0F;
	}
}