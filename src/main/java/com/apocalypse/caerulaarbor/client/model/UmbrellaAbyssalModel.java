package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.UmbrellaAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UmbrellaAbyssalModel extends GeoModel<UmbrellaAbyssalEntity> {
	@Override
	public ResourceLocation getAnimationResource(UmbrellaAbyssalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/umbrella.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UmbrellaAbyssalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/umbrella.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UmbrellaAbyssalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
