package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.FloaterProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloaterProkaryoteModel extends GeoModel<FloaterProkaryoteEntity> {
	@Override
	public ResourceLocation getAnimationResource(FloaterProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/floater.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FloaterProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/floater.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FloaterProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
