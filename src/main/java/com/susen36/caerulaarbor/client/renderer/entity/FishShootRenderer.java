package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.client.model.entity.ModelBulletProjectile;
import com.susen36.caerulaarbor.entity.bullets.FishShootEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FishShootRenderer extends EntityRenderer<FishShootEntity> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/fishbullet.png");
	private final ModelBulletProjectile model;

	public FishShootRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ModelBulletProjectile(context.bakeLayer(ModelBulletProjectile.LAYER_LOCATION));
	}

	@Override
	public void render(FishShootEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(FishShootEntity entity) {
		return texture;
	}
}
