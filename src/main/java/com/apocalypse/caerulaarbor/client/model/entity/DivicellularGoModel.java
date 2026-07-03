package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.DivicellularGoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DivicellularGoModel extends GeoModel<DivicellularGoEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/accumulator.png");

	@Override
	public ResourceLocation getAnimationResource(DivicellularGoEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/accumulator_pet.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DivicellularGoEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/accumulator_pet.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DivicellularGoEntity entity) {
		return TEXTURE;
	}

}
