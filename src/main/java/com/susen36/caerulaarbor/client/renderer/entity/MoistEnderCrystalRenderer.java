package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.susen36.caerulaarbor.client.model.entity.MoistEnderCrystalModel;
import com.susen36.caerulaarbor.client.model.entity.layer.MoistEnderCrystalLayer;
import com.susen36.caerulaarbor.entity.MoistEnderCrystalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoistEnderCrystalRenderer extends GeoEntityRenderer<MoistEnderCrystalEntity> {
    private static final RenderType BEAM = RenderType.entitySmoothCutout(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/end_crystal/end_crystal_beam.png"));

    public MoistEnderCrystalRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MoistEnderCrystalModel());
        this.shadowRadius = 0f;
        this.addRenderLayer(new MoistEnderCrystalLayer(this));
    }

    @Override
    public RenderType getRenderType(MoistEnderCrystalEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public int getPackedOverlay(MoistEnderCrystalEntity animatable, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(animatable.hurtTime > 0 || animatable.deathTime > 0 ? 0.35F : u), OverlayTexture.v(false));
    }

    @Override
    public void preRender(PoseStack poseStack, MoistEnderCrystalEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    protected float getDeathMaxRotation(MoistEnderCrystalEntity entityLivingBaseIn) {
        return 0.0F;
    }

    private double lerpX(Entity entity, float partialTicks) {
        return Mth.lerp(partialTicks, entity.xo, entity.getX());
    }

    private double lerpY(Entity entity, float partialTicks) {
        return Mth.lerp(partialTicks, entity.yo, entity.getY());
    }

    private double lerpZ(Entity entity, float partialTicks) {
        return Mth.lerp(partialTicks, entity.zo, entity.getZ());
    }

    @Override
    public void render(MoistEnderCrystalEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        Entity owner = entity.getOwner();
        if (owner != null && owner.isAlive()) {
            //水晶光束
            float pX = (float) (this.lerpX(owner, partialTick) - this.lerpX(entity, partialTick));
            float pY = (float) (this.lerpY(owner, partialTick) - this.lerpY(entity, partialTick));
            float pZ = (float) (this.lerpZ(owner, partialTick) - this.lerpZ(entity, partialTick));
            float f = Mth.sqrt(pX * pX + pZ * pZ);
            float f1 = Mth.sqrt(pX * pX + pY * pY + pZ * pZ);
            poseStack.pushPose();
            poseStack.translate(0.0f, 1.0f, 0.0f);
            poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(pZ, pX)) - 1.5707964f));
            poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(f, pY)) - 1.5707964f));
            VertexConsumer vertexconsumer = bufferSource.getBuffer(BEAM);
            float f2 = 0.0f - ((float) entity.tickCount + partialTick) * 0.01f;
            float f3 = Mth.sqrt(pX * pX + pY * pY + pZ * pZ) / 32.0f - ((float) entity.tickCount + partialTick) * 0.01f;
            float f4 = 0.0f;
            float f5 = 0.75f;
            float f6 = 0.0f;
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            for (int j = 1; j <= 4; ++j) {
                float f7 = Mth.sin((float) j * ((float) Math.PI * 2) / 4.0f) * 0.75f;
                float f8 = Mth.cos((float) j * ((float) Math.PI * 2) / 4.0f) * 0.75f;
                float f9 = (float) j / 8.0f;
                vertexconsumer.addVertex(matrix4f, f4 * 0.5f, f5 * 0.5f, 0.0f).setColor(232, 197, 246, 255).setUv(f6, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0f, -1.0f, 0.0f);
                vertexconsumer.addVertex(matrix4f, f4 * 0.125f, f5 * 0.125f, f1).setColor(181, 227, 238, 255).setUv(f6, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0f, -1.0f, 0.0f);
                vertexconsumer.addVertex(matrix4f, f7 * 0.125f, f8 * 0.125f, f1).setColor(181, 227, 238, 255).setUv(f9, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0f, -1.0f, 0.0f);
                vertexconsumer.addVertex(matrix4f, f7 * 0.5f, f8 * 0.5f, 0.0f).setColor(232, 197, 246, 255).setUv(f9, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0f, -1.0f, 0.0f);
                f4 = f7;
                f5 = f8;
                f6 = f9;
            }
            poseStack.popPose();
        } else {
            entity.owner = null;
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}