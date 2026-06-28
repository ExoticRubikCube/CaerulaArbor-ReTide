
package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.ComplexChitinGolemModel;
import com.apocalypse.caerulaarbor.client.model.entity.layer.ComplexChitinGolemLayer;
import com.apocalypse.caerulaarbor.entity.ComplexChitinGolemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ComplexChitinGolemRenderer extends GeoEntityRenderer<ComplexChitinGolemEntity> {
	public ComplexChitinGolemRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ComplexChitinGolemModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new ComplexChitinGolemLayer(this));
	}

	@Override
	public RenderType getRenderType(ComplexChitinGolemEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, ComplexChitinGolemEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(ComplexChitinGolemEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
