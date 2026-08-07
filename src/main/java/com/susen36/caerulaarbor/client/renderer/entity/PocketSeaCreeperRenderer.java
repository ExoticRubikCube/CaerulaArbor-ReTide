package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.PocketSeaCreeperModel;
import com.susen36.caerulaarbor.client.model.entity.layer.PocketSeaCreeperLayer;
import com.susen36.caerulaarbor.client.model.entity.layer.PocketSeaCreeperPowerLayer;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCreeperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PocketSeaCreeperRenderer extends GeoEntityRenderer<PocketSeaCreeperEntity> {
	public PocketSeaCreeperRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new PocketSeaCreeperModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new PocketSeaCreeperLayer(this));
		this.addRenderLayer(new PocketSeaCreeperPowerLayer(this));
	}

	@Override
	public RenderType getRenderType(PocketSeaCreeperEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, PocketSeaCreeperEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		float swellProgress = entity.getSwelling(partialTick);
		float baseScale = 1.75f;

		if (swellProgress > 0.0F) {
			float f = swellProgress;
			float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
			f = Mth.clamp(f, 0.0F, 1.0F);
			f *= f;
			f *= f;
			float swellingScaleXZ = (1.0F + f * 0.4F) * f1;
			float swellingScaleY = (1.0F + f * 0.1F) / f1;
			this.scaleWidth = baseScale * swellingScaleXZ;
			this.scaleHeight = baseScale * swellingScaleY;
		} else {
			this.scaleWidth = baseScale;
			this.scaleHeight = baseScale;
		}

		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	public int getPackedOverlay(PocketSeaCreeperEntity animatable, float u, float partialTick) {
		float swellProgress = animatable.getSwelling(partialTick);
		if (swellProgress > 0.0F) {
			float whiteOverlay = (int)(swellProgress * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swellProgress, 0.5F, 1.0F);
			return OverlayTexture.pack(OverlayTexture.u(whiteOverlay),
					OverlayTexture.v(animatable.hurtTime > 0 || animatable.deathTime > 0));
		}
		return super.getPackedOverlay(animatable, u, partialTick);
	}

	@Override
	protected float getDeathMaxRotation(PocketSeaCreeperEntity entityLivingBaseIn) {
		return 0.0F;
	}
}