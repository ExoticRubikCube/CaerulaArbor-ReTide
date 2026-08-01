package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.susen36.caerulaarbor.client.model.entity.OceanizedEnderDragonModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedEnderDragonLayer;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEnderDragonRenderer extends GeoEntityRenderer<OceanizedEnderDragonEntity> {
	public OceanizedEnderDragonRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEnderDragonModel());
		this.shadowRadius = 4f;
		this.addRenderLayer(new OceanizedEnderDragonLayer(this));
	}

	@Override
	protected void applyRotations(OceanizedEnderDragonEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
		float yaw = (float) animatable.getLatencyYRot(7, partialTick);
		poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
	}

	@Override
	protected float getDeathMaxRotation(OceanizedEnderDragonEntity entityLivingBaseIn) {
		return 0.0F;
	}
}