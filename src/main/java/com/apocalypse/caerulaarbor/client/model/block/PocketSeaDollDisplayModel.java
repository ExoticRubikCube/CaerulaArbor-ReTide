package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.item.PocketSeaDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PocketSeaDollDisplayModel extends GeoModel<PocketSeaDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(PocketSeaDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/pocket_sea_creeper_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/pocket_sea_creeper_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaDollDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/pocket_sea_creeper.png");
	}
}
