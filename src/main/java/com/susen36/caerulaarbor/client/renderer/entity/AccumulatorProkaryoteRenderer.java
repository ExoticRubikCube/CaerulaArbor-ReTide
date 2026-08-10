
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.AccumulatorProkaryoteModel;
import com.susen36.caerulaarbor.client.model.entity.layer.AccumulatorProkaryoteLayer;
import com.susen36.caerulaarbor.entity.AccumulatorProkaryoteEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AccumulatorProkaryoteRenderer extends GeoEntityRenderer<AccumulatorProkaryoteEntity> {
	public AccumulatorProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AccumulatorProkaryoteModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new AccumulatorProkaryoteLayer(this));
	}
}