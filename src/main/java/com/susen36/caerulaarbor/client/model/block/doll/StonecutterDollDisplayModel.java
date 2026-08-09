package com.susen36.caerulaarbor.client.model.block.doll;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.item.doll.StonecutterDollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StonecutterDollDisplayModel extends GeoModel<StonecutterDollDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(StonecutterDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/stonecutter_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(StonecutterDollDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/stonecutter_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(StonecutterDollDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/stonecutter_doll.png");
	}
}