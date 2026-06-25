package com.apocalypse.caerulaarbor.item.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.item.TrailriteArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrailriteArmorModel extends GeoModel<TrailriteArmorItem> {
	@Override
	public ResourceLocation getAnimationResource(TrailriteArmorItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/trairite_armor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TrailriteArmorItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/trairite_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TrailriteArmorItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/item/trailrite_armor.png");
	}
}
