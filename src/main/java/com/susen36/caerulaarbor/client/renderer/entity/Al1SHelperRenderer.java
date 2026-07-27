
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.Al1SHelperModel;
import com.susen36.caerulaarbor.client.model.entity.layer.Al1SHelperLayer;
import com.susen36.caerulaarbor.entity.helper.Al1SHelperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Al1SHelperRenderer extends GeoEntityRenderer<Al1SHelperEntity> {
	public Al1SHelperRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new Al1SHelperModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new Al1SHelperLayer(this));
	}

	@Override
	public RenderType getRenderType(Al1SHelperEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, Al1SHelperEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(Al1SHelperEntity entityLivingBaseIn) {
		return 0.0F;
	}
}