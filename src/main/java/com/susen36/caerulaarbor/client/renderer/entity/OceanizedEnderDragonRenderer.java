package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.susen36.caerulaarbor.client.model.entity.OceanizedEnderDragonModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedEnderDragonLayer;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
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
	public void preRender(PoseStack poseStack, OceanizedEnderDragonEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		this.scaleHeight = 1f;
		this.scaleWidth = 1f;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	/*
	 @Deprecated(
        forRemoval = true
    )
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        this.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, 1.0F);
    }

    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        if (this.isShaking(animatable)) {
            rotationYaw += (float)(Math.cos((double)animatable.tickCount * (double)3.25F) * Math.PI * 0.4);
        }

        if (!animatable.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
        }

        if (animatable instanceof LivingEntity livingEntity) {
            if (livingEntity.deathTime > 0) {
                float deathRotation = ((float)livingEntity.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
                poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1.0F) * this.getDeathMaxRotation(animatable)));
            } else if (livingEntity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - livingEntity.getXRot()));
                poseStack.mulPose(Axis.YP.rotationDegrees(((float)livingEntity.tickCount + partialTick) * -75.0F));
            } else if (animatable.hasPose(Pose.SLEEPING)) {
                Direction bedOrientation = livingEntity.getBedOrientation();
                poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
                poseStack.mulPose(Axis.ZP.rotationDegrees(this.getDeathMaxRotation(animatable)));
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            } else if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
                poseStack.translate(0.0F, (animatable.getBbHeight() + 0.1F) / nativeScale, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }

    }
	 */
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