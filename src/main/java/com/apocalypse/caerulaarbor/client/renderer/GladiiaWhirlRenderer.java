
package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.entity.GladiiaWhirlEntity;
import com.apocalypse.caerulaarbor.client.model.layer.GladiiaWhirlLayer;
import com.apocalypse.caerulaarbor.client.model.GladiiaWhirlModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GladiiaWhirlRenderer extends GeoEntityRenderer<GladiiaWhirlEntity> {
	public GladiiaWhirlRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GladiiaWhirlModel());
		this.shadowRadius = 0f;
		this.addRenderLayer(new GladiiaWhirlLayer(this));
	}

	@Override
	public RenderType getRenderType(GladiiaWhirlEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, GladiiaWhirlEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(GladiiaWhirlEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
