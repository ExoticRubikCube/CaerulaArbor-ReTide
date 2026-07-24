package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.OceanizedEnderDragonModel;
import com.apocalypse.caerulaarbor.client.model.entity.layer.OceanizedEnderDragonLayer;
import com.apocalypse.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEnderDragonRenderer extends GeoEntityRenderer<OceanizedEnderDragonEntity> {
	public OceanizedEnderDragonRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEnderDragonModel());
		this.shadowRadius = 4f;
		this.addRenderLayer(new OceanizedEnderDragonLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedEnderDragonEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedEnderDragonEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		this.scaleHeight = 1f;
		this.scaleWidth = 1f;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected void applyRotations(OceanizedEnderDragonEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
		float yaw = (float) animatable.getLatencyYRot(7, partialTick);
		poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
	}

	@Override
	protected float getDeathMaxRotation(OceanizedEnderDragonEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
