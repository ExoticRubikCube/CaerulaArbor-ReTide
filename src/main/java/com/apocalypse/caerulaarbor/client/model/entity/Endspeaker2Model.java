package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.Endspeaker2Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

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
