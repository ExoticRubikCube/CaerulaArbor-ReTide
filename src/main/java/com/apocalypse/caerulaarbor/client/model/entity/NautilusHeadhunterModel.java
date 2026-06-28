package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.NautilusHeadhunterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NautilusHeadhunterModel extends GeoModel<NautilusHeadhunterEntity> {
	@Override
	public ResourceLocation getAnimationResource(NautilusHeadhunterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/nultilus_headhunter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NautilusHeadhunterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/nultilus_headhunter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NautilusHeadhunterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
