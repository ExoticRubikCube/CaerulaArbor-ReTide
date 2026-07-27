
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceannizedWitheriaModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceannizedWitheriaLayer;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitheriaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceannizedWitheriaRenderer extends GeoEntityRenderer<OceanizedWitheriaEntity> {
	public OceannizedWitheriaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceannizedWitheriaModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new OceannizedWitheriaLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedWitheriaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedWitheriaEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1.25f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(OceanizedWitheriaEntity entityLivingBaseIn) {
		return 0.0F;
	}
}