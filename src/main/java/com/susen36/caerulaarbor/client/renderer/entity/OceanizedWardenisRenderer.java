
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedWardenisModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedWardenisLayer;
import com.susen36.caerulaarbor.entity.warden.OceanizedWardenisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedWardenisRenderer extends GeoEntityRenderer<OceanizedWardenisEntity> {
	public OceanizedWardenisRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedWardenisModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedWardenisLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedWardenisEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(OceanizedWardenisEntity entityLivingBaseIn) {
		return 0.0F;
	}
}