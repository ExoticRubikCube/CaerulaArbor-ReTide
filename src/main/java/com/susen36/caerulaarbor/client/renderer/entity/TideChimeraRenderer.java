
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.TideChimeraModel;
import com.susen36.caerulaarbor.client.model.entity.layer.TideChimeraLayer;
import com.susen36.caerulaarbor.entity.TideChimeraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TideChimeraRenderer extends GeoEntityRenderer<TideChimeraEntity> {
	public TideChimeraRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TideChimeraModel());
		this.shadowRadius = 1.5f;
		this.addRenderLayer(new TideChimeraLayer(this));
	}

	@Override
	protected float getDeathMaxRotation(TideChimeraEntity entityLivingBaseIn) {
		return 0.0F;
	}
}