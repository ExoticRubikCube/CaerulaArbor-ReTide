package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.Endspeaker1Entity;

public class Endspeaker1Model extends GeoModel<Endspeaker1Entity> {
	@Override
	public ResourceLocation getAnimationResource(Endspeaker1Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/endspeaker_1.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(Endspeaker1Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/endspeaker_1.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Endspeaker1Entity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
