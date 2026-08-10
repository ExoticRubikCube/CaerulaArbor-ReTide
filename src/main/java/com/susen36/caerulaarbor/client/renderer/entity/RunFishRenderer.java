
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.RunFishModel;
import com.susen36.caerulaarbor.entity.RunFishEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RunFishRenderer extends GeoEntityRenderer<RunFishEntity> {
	public RunFishRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new RunFishModel());
		this.shadowRadius = 0.5f;
	}
}