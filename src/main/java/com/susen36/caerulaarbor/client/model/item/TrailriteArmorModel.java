package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.item.TrailriteArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TrailriteArmorModel extends GeoModel<TrailriteArmorItem> {
	@Override
	public ResourceLocation getAnimationResource(TrailriteArmorItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/trairite_armor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TrailriteArmorItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/trairite_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TrailriteArmorItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/trailrite_armor.png");
	}
}