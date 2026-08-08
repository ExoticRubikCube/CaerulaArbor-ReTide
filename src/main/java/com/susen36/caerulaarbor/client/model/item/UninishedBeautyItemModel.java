package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.item.UninishedBeautyItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UninishedBeautyItemModel extends GeoModel<UninishedBeautyItem> {
	@Override
	public ResourceLocation getAnimationResource(UninishedBeautyItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/unfinished_beautuy.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UninishedBeautyItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/unfinished_beautuy.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UninishedBeautyItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/undone_beauty.png");
	}
}