
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.client.model.entity.ShellSeaRunnerModel;
import com.susen36.caerulaarbor.entity.ShellSeaRunnerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShellSeaRunnerRenderer extends GeoEntityRenderer<ShellSeaRunnerEntity> {
	public ShellSeaRunnerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ShellSeaRunnerModel());
		this.shadowRadius = 0.5f;
	}
}