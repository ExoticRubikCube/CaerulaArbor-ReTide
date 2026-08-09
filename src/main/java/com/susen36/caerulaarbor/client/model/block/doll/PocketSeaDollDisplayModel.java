package com.susen36.caerulaarbor.client.model.block.doll;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.block.item.doll.PocketSeaDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PocketSeaDollDisplayModel extends GeoModel<PocketSeaDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(PocketSeaDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/pocket_sea_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/pocket_sea_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaDollDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/pocket_sea_doll.png");
	}
}