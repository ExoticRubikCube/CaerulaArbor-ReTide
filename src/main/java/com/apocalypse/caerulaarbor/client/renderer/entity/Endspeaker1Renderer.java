
package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.Endspeaker1Model;
import com.apocalypse.caerulaarbor.entity.Endspeaker1Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Endspeaker1Renderer extends GeoEntityRenderer<Endspeaker1Entity> {
	public Endspeaker1Renderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new Endspeaker1Model());
		this.shadowRadius = 0.7f;
	}

	@Override
	public RenderType getRenderType(Endspeaker1Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, Endspeaker1Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(Endspeaker1Entity entityLivingBaseIn) {
		return 0.0F;
	}
}
