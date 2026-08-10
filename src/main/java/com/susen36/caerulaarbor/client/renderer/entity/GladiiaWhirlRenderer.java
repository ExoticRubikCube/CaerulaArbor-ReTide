
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.GladiiaWhirlModel;
import com.susen36.caerulaarbor.client.model.entity.layer.GladiiaWhirlLayer;
import com.susen36.caerulaarbor.entity.GladiiaWhirlEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GladiiaWhirlRenderer extends GeoEntityRenderer<GladiiaWhirlEntity> {
	public GladiiaWhirlRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GladiiaWhirlModel());
		this.shadowRadius = 0f;
		this.addRenderLayer(new GladiiaWhirlLayer(this));
	}

	@Override
	protected float getDeathMaxRotation(GladiiaWhirlEntity entityLivingBaseIn) {
		return 0.0F;
	}
}