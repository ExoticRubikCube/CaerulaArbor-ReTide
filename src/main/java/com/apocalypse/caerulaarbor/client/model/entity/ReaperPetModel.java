package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.entity.ReaperPetEntity;

public class ReaperPetModel extends GeoModel<ReaperPetEntity> {
	@Override
	public ResourceLocation getAnimationResource(ReaperPetEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/reaperpet.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ReaperPetEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/reaperpet.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ReaperPetEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
