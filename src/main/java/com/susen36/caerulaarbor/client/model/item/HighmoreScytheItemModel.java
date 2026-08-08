package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.HighmoreScytheItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreScytheItemModel extends GeoModel<HighmoreScytheItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreScytheItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/highmorescythe.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreScytheItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/highmorescythe.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreScytheItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/highmore_scythe_bladed.png");
	}
}