
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.FeederProkaryoteModel;
import com.susen36.caerulaarbor.client.model.entity.layer.FeederProkaryoteLayer;
import com.susen36.caerulaarbor.entity.FeederProkaryoteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FeederProkaryoteRenderer extends GeoEntityRenderer<FeederProkaryoteEntity> {
	public FeederProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FeederProkaryoteModel());
		this.shadowRadius = 0.8f;
		this.addRenderLayer(new FeederProkaryoteLayer(this));
	}

	@Override
	protected float getDeathMaxRotation(FeederProkaryoteEntity entityLivingBaseIn) {
		return 0.0F;
	}
}