
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.QunyouWantedIsharmlaEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.QunyouWantedIsharmlaLayer;
import com.susen36.caerulaarbor.client.model.entity.QunyouWantedIsharmlaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class QunyouWantedIsharmlaRenderer extends GeoEntityRenderer<QunyouWantedIsharmlaEntity> {
	public QunyouWantedIsharmlaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new QunyouWantedIsharmlaModel());
		this.shadowRadius = 5f;
		this.addRenderLayer(new QunyouWantedIsharmlaLayer(this));
	}

	@Override
	public RenderType getRenderType(QunyouWantedIsharmlaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, QunyouWantedIsharmlaEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 3f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
