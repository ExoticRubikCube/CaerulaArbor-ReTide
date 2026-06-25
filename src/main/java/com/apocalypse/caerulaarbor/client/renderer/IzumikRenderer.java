
package com.apocalypse.caerulaarbor.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.apocalypse.caerulaarbor.client.model.IzumikModel;
import com.apocalypse.caerulaarbor.client.model.layer.IzumikLayer;
import com.apocalypse.caerulaarbor.entity.IzumikEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class IzumikRenderer extends GeoEntityRenderer<IzumikEntity> {
	public IzumikRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new IzumikModel());
		this.shadowRadius = 3f;
		this.addRenderLayer(new IzumikLayer(this));
	}

	@Override
	public RenderType getRenderType(IzumikEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, IzumikEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(IzumikEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
