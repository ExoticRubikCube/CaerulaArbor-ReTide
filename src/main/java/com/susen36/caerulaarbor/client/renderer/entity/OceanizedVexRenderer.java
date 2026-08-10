
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.OceanizedVexModel;
import com.susen36.caerulaarbor.client.model.entity.layer.OceanizedVexLayer;
import com.susen36.caerulaarbor.entity.OceanizedVexEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedVexRenderer extends GeoEntityRenderer<OceanizedVexEntity> {
	public OceanizedVexRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedVexModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new OceanizedVexLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedVexEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(OceanizedVexEntity entityLivingBaseIn) {
		return 0.0F;
	}
}