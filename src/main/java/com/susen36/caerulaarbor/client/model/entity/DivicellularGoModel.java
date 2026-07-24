package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.DivicellularGoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DivicellularGoModel extends GeoModel<DivicellularGoEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/accumulator.png");

	@Override
	public ResourceLocation getAnimationResource(DivicellularGoEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/accumulator_pet.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DivicellularGoEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/accumulator_pet.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DivicellularGoEntity entity) {
		return TEXTURE;
	}

}
