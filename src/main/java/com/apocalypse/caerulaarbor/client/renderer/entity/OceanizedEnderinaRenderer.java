package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.OceanizedEnderinaModel;
import com.apocalypse.caerulaarbor.client.model.entity.layer.OceanizedEnderinaLayer;
import com.apocalypse.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEnderinaRenderer extends GeoEntityRenderer<OceanizedEnderinaEntity> {
	public OceanizedEnderinaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEnderinaModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedEnderinaLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedEnderinaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedEnderinaEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(OceanizedEnderinaEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
