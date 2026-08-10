
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.NetherseaSlimeModel;
import com.susen36.caerulaarbor.entity.slime.NetherseaSlimeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NetherseaSlimeRenderer extends GeoEntityRenderer<NetherseaSlimeEntity> {
	public NetherseaSlimeRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new NetherseaSlimeModel());
		this.shadowRadius = 0f;
	}

	@Override
	public RenderType getRenderType(NetherseaSlimeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, NetherseaSlimeEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = (float) entity.getSlimeSize();
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	protected float getDeathMaxRotation(NetherseaSlimeEntity entityLivingBaseIn) {
		return 0.0F;
	}
}