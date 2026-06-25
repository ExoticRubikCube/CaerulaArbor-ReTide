
package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.entity.HighmoreEntity;
import com.apocalypse.caerulaarbor.client.model.layer.HighmoreLayer;
import com.apocalypse.caerulaarbor.client.model.HighmoreModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HighmoreRenderer extends GeoEntityRenderer<HighmoreEntity> {
	public HighmoreRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new HighmoreModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new HighmoreLayer(this));
	}

	@Override
	public RenderType getRenderType(HighmoreEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, HighmoreEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 2f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(HighmoreEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
