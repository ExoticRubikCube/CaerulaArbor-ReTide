package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.IzumikOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IzumikOffspringModel extends GeoModel<IzumikOffspringEntity> {
	@Override
	public ResourceLocation getAnimationResource(IzumikOffspringEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/izumik_offspring.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IzumikOffspringEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/izumik_offspring.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IzumikOffspringEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
