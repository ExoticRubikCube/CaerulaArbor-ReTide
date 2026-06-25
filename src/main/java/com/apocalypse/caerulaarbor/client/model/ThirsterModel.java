package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.ThirsterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThirsterModel extends GeoModel<ThirsterEntity> {
	@Override
	public ResourceLocation getAnimationResource(ThirsterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/thirster.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ThirsterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/thirster.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ThirsterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
