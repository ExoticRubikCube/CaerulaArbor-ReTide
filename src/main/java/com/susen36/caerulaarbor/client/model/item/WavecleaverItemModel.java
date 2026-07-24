package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.item.WavecleaverItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WavecleaverItemModel extends GeoModel<WavecleaverItem> {
	@Override
	public ResourceLocation getAnimationResource(WavecleaverItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/wavelceaver.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WavecleaverItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/wavelceaver.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WavecleaverItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/weavecleaver.png");
	}
}
