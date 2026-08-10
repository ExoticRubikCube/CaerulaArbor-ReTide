
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.GladiiaModel;
import com.susen36.caerulaarbor.client.model.entity.layer.GladiiaLayer;
import com.susen36.caerulaarbor.entity.GladiiaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GladiiaRenderer extends GeoEntityRenderer<GladiiaEntity> {
	public GladiiaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GladiiaModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new GladiiaLayer(this));
	}

	@Override
	protected float getDeathMaxRotation(GladiiaEntity entityLivingBaseIn) {
		return 0.0F;
	}
}