package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.SpecterDollEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpecterDollModel extends GeoModel<SpecterDollEntity> {
	@Override
	public ResourceLocation getAnimationResource(SpecterDollEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/specter_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SpecterDollEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/specter_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpecterDollEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
