package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.block.display.PocketSeaDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PocketSeaDollDisplayModel extends GeoModel<PocketSeaDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(PocketSeaDollDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/creeper_fish_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaDollDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/creeper_fish_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaDollDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/creeperfish.png");
	}
}
