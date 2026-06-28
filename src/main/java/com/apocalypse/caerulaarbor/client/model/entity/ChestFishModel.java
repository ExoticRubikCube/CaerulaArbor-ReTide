package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ChestFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChestFishModel extends GeoModel<ChestFishEntity> {
	@Override
	public ResourceLocation getAnimationResource(ChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chest_fish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chest_fish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChestFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
