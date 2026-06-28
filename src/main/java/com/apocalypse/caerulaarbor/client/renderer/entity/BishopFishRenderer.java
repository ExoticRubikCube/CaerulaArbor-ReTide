
package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.BishopFishModel;
import com.apocalypse.caerulaarbor.client.model.entity.layer.BishopFishLayer;
import com.apocalypse.caerulaarbor.entity.BishopFishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BishopFishRenderer extends GeoEntityRenderer<BishopFishEntity> {
	public BishopFishRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new BishopFishModel());
		this.shadowRadius = 1.5f;
		this.addRenderLayer(new BishopFishLayer(this));
	}

	@Override
	public RenderType getRenderType(BishopFishEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, BishopFishEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(BishopFishEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
