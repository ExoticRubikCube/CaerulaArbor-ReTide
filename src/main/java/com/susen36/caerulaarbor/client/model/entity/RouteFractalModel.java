package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.shaper.RouteFractalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RouteFractalModel extends GeoModel<RouteFractalEntity> {
	@Override
	public ResourceLocation getAnimationResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/blackroute.png");
	}

}