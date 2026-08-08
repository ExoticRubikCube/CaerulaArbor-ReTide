package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.CircularSawItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CircularSawItemModel extends GeoModel<CircularSawItem> {
	@Override
	public ResourceLocation getAnimationResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/circular_saw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/circular_saw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/saw.png");
	}
}