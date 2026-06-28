package com.apocalypse.caerulaarbor.client.model.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.item.UninishedBeautyItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UninishedBeautyItemModel extends GeoModel<UninishedBeautyItem> {
	@Override
	public ResourceLocation getAnimationResource(UninishedBeautyItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/unfinished_beautuy.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(UninishedBeautyItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/unfinished_beautuy.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(UninishedBeautyItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/item/undone_beauty.png");
	}
}
