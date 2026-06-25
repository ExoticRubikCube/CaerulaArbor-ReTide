package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.AccumulatorCloneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AccumulatorCloneModel extends GeoModel<AccumulatorCloneEntity> {
	@Override
	public ResourceLocation getAnimationResource(AccumulatorCloneEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/accumulator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AccumulatorCloneEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/accumulator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AccumulatorCloneEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
