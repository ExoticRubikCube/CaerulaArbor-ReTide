package com.apocalypse.caerulaarbor.item.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.item.HighmoreScytheItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreScytheItemModel extends GeoModel<HighmoreScytheItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreScytheItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmorescythe.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreScytheItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmorescythe.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreScytheItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/item/highmore_scythe_bladed.png");
	}
}
