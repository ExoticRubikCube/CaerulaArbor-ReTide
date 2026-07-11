package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.routeshaper.RouteShaperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RouteShaperModel extends GeoModel<RouteShaperEntity> {
	@Override
	public ResourceLocation getAnimationResource(RouteShaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RouteShaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RouteShaperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/routeshaper.png");
	}

}

