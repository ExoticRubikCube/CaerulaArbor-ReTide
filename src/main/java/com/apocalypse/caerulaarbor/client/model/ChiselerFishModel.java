package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.ChiselerFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChiselerFishModel extends GeoModel<ChiselerFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(ChiselerFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chiseler.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChiselerFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chiseler.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChiselerFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
