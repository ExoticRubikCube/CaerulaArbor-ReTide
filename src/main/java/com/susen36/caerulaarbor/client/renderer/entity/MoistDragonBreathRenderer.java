
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.MoistDragonBreathModel;
import com.susen36.caerulaarbor.client.model.entity.layer.MoistDragonBreathLayer;
import com.susen36.caerulaarbor.entity.enderdragon.MoistDragonBreathEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoistDragonBreathRenderer extends GeoEntityRenderer<MoistDragonBreathEntity> {
	public MoistDragonBreathRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MoistDragonBreathModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new MoistDragonBreathLayer(this));
	}

	@Override
	public RenderType getRenderType(MoistDragonBreathEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public int getPackedOverlay(MoistDragonBreathEntity animatable, float u, float partialTick) {
		return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(false));
	}

	@Override
	protected float getDeathMaxRotation(MoistDragonBreathEntity entityLivingBaseIn) {
		return 0.0F;
	}
}