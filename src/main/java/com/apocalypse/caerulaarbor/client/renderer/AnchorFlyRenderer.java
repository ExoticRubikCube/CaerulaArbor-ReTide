package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.client.model.ModelAnchorFly;
import com.apocalypse.caerulaarbor.entity.AnchorFlyEntity;
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

public class AnchorFlyRenderer extends EntityRenderer<AnchorFlyEntity> {
	private static final ResourceLocation texture = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/weapon_anchor_fly.png");
	private final ModelAnchorFly model;

	public AnchorFlyRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ModelAnchorFly(context.bakeLayer(ModelAnchorFly.LAYER_LOCATION));
	}

	@Override
	public void render(AnchorFlyEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(AnchorFlyEntity entity) {
		return texture;
	}
}
