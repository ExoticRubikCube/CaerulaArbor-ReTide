package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.item.SwarmcallerDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwarmcallerDollDisplayModel extends GeoModel<SwarmcallerDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(SwarmcallerDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/umbrella_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SwarmcallerDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/umbrella_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SwarmcallerDollDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/umbrella.png");
	}
}