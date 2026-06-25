
package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.entity.OceanizedIllusionerEntity;
import com.apocalypse.caerulaarbor.client.model.layer.OceanizedIllusionerLayer;
import com.apocalypse.caerulaarbor.client.model.OceanizedIllusionerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedIllusionerRenderer extends GeoEntityRenderer<OceanizedIllusionerEntity> {
	public OceanizedIllusionerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedIllusionerModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new OceanizedIllusionerLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedIllusionerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedIllusionerEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(OceanizedIllusionerEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
