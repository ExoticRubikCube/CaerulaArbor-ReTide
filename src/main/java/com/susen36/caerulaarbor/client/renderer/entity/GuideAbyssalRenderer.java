
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.GuideAbyssalModel;
import com.susen36.caerulaarbor.entity.GuideAbyssalEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GuideAbyssalRenderer extends GeoEntityRenderer<GuideAbyssalEntity> {
	public GuideAbyssalRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GuideAbyssalModel());
		this.shadowRadius = 0.6f;
	}

	@Override
	protected float getDeathMaxRotation(GuideAbyssalEntity entityLivingBaseIn) {
		return 0.0F;
	}
}