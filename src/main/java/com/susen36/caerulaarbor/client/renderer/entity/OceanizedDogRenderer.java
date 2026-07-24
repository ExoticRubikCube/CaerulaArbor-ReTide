
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedDogModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedDogLayer;
import com.susen36.caerulaarbor.entity.OceanizedDogEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedDogRenderer extends GeoEntityRenderer<OceanizedDogEntity> {
	public OceanizedDogRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedDogModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedDogLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedDogEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedDogEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
