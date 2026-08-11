
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.IsharmlaTearModel;
import com.susen36.caerulaarbor.client.model.entity.layer.IsharmlaTearLayer;
import com.susen36.caerulaarbor.entity.isharmla.IsharmlaTearEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IsharmlaTearRenderer extends GeoEntityRenderer<IsharmlaTearEntity> {
	public IsharmlaTearRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new IsharmlaTearModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new IsharmlaTearLayer(this));
	}

	@Override
	public RenderType getRenderType(IsharmlaTearEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(IsharmlaTearEntity entityLivingBaseIn) {
		return 0.0F;
	}
}