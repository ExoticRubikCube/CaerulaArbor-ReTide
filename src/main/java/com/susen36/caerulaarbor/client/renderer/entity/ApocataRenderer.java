
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.ApocataModel;
import com.susen36.caerulaarbor.entity.ApocataEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ApocataRenderer extends GeoEntityRenderer<ApocataEntity> {
	public ApocataRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ApocataModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	protected float getDeathMaxRotation(ApocataEntity entityLivingBaseIn) {
		return 0.0F;
	}
}