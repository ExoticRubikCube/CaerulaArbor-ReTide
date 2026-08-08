package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.routeshaper.LingeringFractalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LingeringFractalModel extends GeoModel<LingeringFractalEntity> {
	@Override
	public ResourceLocation getAnimationResource(LingeringFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LingeringFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LingeringFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/lingering_fractal.png");
	}

}