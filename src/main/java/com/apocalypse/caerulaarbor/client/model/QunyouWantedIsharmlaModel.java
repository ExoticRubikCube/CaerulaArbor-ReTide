package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.QunyouWantedIsharmlaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class QunyouWantedIsharmlaModel extends GeoModel<QunyouWantedIsharmlaEntity> {
	@Override
	public ResourceLocation getAnimationResource(QunyouWantedIsharmlaEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/isharmla.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(QunyouWantedIsharmlaEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/isharmla.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(QunyouWantedIsharmlaEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
