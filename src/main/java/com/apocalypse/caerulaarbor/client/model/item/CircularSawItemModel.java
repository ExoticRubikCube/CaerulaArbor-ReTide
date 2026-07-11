package com.apocalypse.caerulaarbor.client.model.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.item.CircularSawItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CircularSawItemModel extends GeoModel<CircularSawItem> {
	@Override
	public ResourceLocation getAnimationResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/circular_saw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/circular_saw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CircularSawItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/saw.png");
	}
}
