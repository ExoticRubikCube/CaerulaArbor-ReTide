package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.client.model.entity.ModelSkimmingSeaBullet;
import com.susen36.caerulaarbor.entity.bullets.SkimmingSeaBulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkimmingSeaBulletRenderer extends EntityRenderer<SkimmingSeaBulletEntity> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/skimming_sea_bullet.png");
	private final ModelSkimmingSeaBullet model;

	public SkimmingSeaBulletRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ModelSkimmingSeaBullet(context.bakeLayer(ModelSkimmingSeaBullet.LAYER_LOCATION));
	}

	@Override
	public void render(SkimmingSeaBulletEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(SkimmingSeaBulletEntity entity) {
		return texture;
	}
}
