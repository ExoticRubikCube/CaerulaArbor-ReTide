
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.FlamarineStatueModel;
import com.susen36.caerulaarbor.entity.FlamarineStatueEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlamarineStatueRenderer extends GeoEntityRenderer<FlamarineStatueEntity> {
	public FlamarineStatueRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FlamarineStatueModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	protected float getDeathMaxRotation(FlamarineStatueEntity entityLivingBaseIn) {
		return 0.0F;
	}
}