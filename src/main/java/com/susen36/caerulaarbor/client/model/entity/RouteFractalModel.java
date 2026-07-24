package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.routeshaper.RouteFractalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RouteFractalModel extends GeoModel<RouteFractalEntity> {
	@Override
	public ResourceLocation getAnimationResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RouteFractalEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/blackroute.png");
	}

}

