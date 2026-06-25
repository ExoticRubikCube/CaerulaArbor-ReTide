package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.IzumikEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IzumikModel extends GeoModel<IzumikEntity> {
	@Override
	public ResourceLocation getAnimationResource(IzumikEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/izumik.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IzumikEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/izumik.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IzumikEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
