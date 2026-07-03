package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SplasherAbyssalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SplasherAbyssalModel extends GeoModel<SplasherAbyssalEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/splasher.png");

	@Override
	public ResourceLocation getAnimationResource(SplasherAbyssalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/splasher.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SplasherAbyssalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/splasher.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SplasherAbyssalEntity entity) {
		return TEXTURE;
	}

}
