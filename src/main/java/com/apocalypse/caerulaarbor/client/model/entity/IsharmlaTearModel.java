package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IsharmlaTearEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IsharmlaTearModel extends GeoModel<IsharmlaTearEntity> {
	@Override
	public ResourceLocation getAnimationResource(IsharmlaTearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/isharmla_tear.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IsharmlaTearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/isharmla_tear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IsharmlaTearEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
