package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.Endspeaker2Entity;

public class Endspeaker2Model extends GeoModel<Endspeaker2Entity> {
	@Override
	public ResourceLocation getAnimationResource(Endspeaker2Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/endspeaker_2.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(Endspeaker2Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/endspeaker_2.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Endspeaker2Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
