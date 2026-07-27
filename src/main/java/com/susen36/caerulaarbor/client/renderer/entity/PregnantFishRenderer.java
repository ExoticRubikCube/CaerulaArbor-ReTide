
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.PregnantFishEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.PregnantFishLayer;
import com.susen36.caerulaarbor.client.model.entity.PregnantFishModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PregnantFishRenderer extends GeoEntityRenderer<PregnantFishEntity> {
	public PregnantFishRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PregnantFishModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new PregnantFishLayer(this));
	}

	@Override
	public RenderType getRenderType(PregnantFishEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, PregnantFishEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}