package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.FissionProkaryoteSlimeModel;
import com.susen36.caerulaarbor.client.model.entity.layer.FissionProkaryoteSlimeLayer;
import com.susen36.caerulaarbor.entity.slime.FissionProkaryoteSlimeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FissionProkaryoteSlimeRenderer extends GeoEntityRenderer<FissionProkaryoteSlimeEntity> {
	public FissionProkaryoteSlimeRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FissionProkaryoteSlimeModel());
		this.shadowRadius = 0f;
		this.addRenderLayer(new FissionProkaryoteSlimeLayer(this));
	}

	@Override
	public RenderType getRenderType(FissionProkaryoteSlimeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, FissionProkaryoteSlimeEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = (float) entity.getSlimeSize();
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		int size = entity.getEntityData().get(FissionProkaryoteSlimeEntity.DATA_SIZE);

		float alpha = Mth.clamp(0.75F + (size - 1.0F) * (0.25F / 3.0F), 0.75F, 1.0F);
		int alphaByte = (int)(alpha * 255.0F) & 0xFF;
		int tintedColor = (alphaByte << 24) | (color & 0x00FFFFFF);
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, tintedColor);}

	@Override
	protected float getDeathMaxRotation(FissionProkaryoteSlimeEntity entityLivingBaseIn) {
		return 0.0F;
	}
}