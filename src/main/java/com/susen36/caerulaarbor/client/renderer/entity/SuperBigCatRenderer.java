
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.SuperBigCatEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.SuperBigCatLayer;
import com.susen36.caerulaarbor.client.model.entity.SuperBigCatModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SuperBigCatRenderer extends GeoEntityRenderer<SuperBigCatEntity> {
	public SuperBigCatRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new SuperBigCatModel());
		this.shadowRadius = 4f;
		this.addRenderLayer(new SuperBigCatLayer(this));
	}

	@Override
	public RenderType getRenderType(SuperBigCatEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, SuperBigCatEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 8f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(SuperBigCatEntity entityLivingBaseIn) {
		return 0.0F;
	}
}