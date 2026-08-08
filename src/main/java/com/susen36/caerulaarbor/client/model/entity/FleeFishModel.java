package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FleeFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FleeFishModel extends GeoModel<FleeFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/fleefish.png");

	@Override
	public ResourceLocation getAnimationResource(FleeFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/fleefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FleeFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/fleefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FleeFishEntity entity) {
		return TEXTURE;
	}

}