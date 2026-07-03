package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.FleeFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FleeFishModel extends GeoModel<FleeFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/fleefish.png");

	@Override
	public ResourceLocation getAnimationResource(FleeFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/fleefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FleeFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/fleefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FleeFishEntity entity) {
		return TEXTURE;
	}

}
