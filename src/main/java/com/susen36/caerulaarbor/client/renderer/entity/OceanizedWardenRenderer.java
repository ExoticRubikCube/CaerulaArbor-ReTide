
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedWardenModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedWardenLayer;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedWardenRenderer extends GeoEntityRenderer<OceanizedWardenEntity> {
	public OceanizedWardenRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedWardenModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new OceanizedWardenLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedWardenEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}


	@Override
	protected float getDeathMaxRotation(OceanizedWardenEntity entityLivingBaseIn) {
		return 0.0F;
	}
}