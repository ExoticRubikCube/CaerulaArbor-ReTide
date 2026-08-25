package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedEnderinaModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedEnderinaLayer;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEnderinaRenderer extends GeoEntityRenderer<OceanizedEnderinaEntity> {
	public OceanizedEnderinaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEnderinaModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedEnderinaLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedEnderinaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(OceanizedEnderinaEntity entityLivingBaseIn) {
		return 0.0F;
	}
}