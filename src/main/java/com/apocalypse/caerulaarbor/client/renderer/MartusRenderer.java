
package com.apocalypse.caerulaarbor.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.apocalypse.caerulaarbor.client.model.MartusModel;
import com.apocalypse.caerulaarbor.client.model.layer.MartusLayer;
import com.apocalypse.caerulaarbor.entity.MartusEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class MartusRenderer extends GeoEntityRenderer<MartusEntity> {
	public MartusRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MartusModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new MartusLayer(this));
	}

	@Override
	public RenderType getRenderType(MartusEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, MartusEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(MartusEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
