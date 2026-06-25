package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.FeederProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FeederProkaryoteModel extends GeoModel<FeederProkaryoteEntity> {
	@Override
	public ResourceLocation getAnimationResource(FeederProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/feeder.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FeederProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/feeder.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FeederProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
