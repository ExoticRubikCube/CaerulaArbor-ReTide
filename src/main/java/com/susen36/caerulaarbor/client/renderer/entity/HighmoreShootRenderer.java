package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.client.model.entity.ModelHighmoreShoot;
import com.susen36.caerulaarbor.entity.bullets.HighmoreShootEntity;
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

public class HighmoreShootRenderer extends EntityRenderer<HighmoreShootEntity> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/highmore_spell.png");
	private final ModelHighmoreShoot model;

	public HighmoreShootRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ModelHighmoreShoot(context.bakeLayer(ModelHighmoreShoot.LAYER_LOCATION));
	}

	@Override
	public void render(HighmoreShootEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(HighmoreShootEntity entity) {
		return texture;
	}
}
