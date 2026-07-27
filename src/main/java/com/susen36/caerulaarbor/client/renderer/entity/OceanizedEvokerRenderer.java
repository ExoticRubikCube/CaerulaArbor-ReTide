
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedEvokerModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedEvokerLayer;
import com.susen36.caerulaarbor.entity.OceanizedEvokerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEvokerRenderer extends GeoEntityRenderer<OceanizedEvokerEntity> {
	public OceanizedEvokerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEvokerModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new OceanizedEvokerLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedEvokerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedEvokerEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}
}