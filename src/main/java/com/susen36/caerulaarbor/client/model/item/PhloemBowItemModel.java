package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArborMod;

import com.susen36.caerulaarbor.item.PhloemBowItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PhloemBowItemModel extends GeoModel<PhloemBowItem> {
	@Override
	public ResourceLocation getAnimationResource(PhloemBowItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/pholemnbow.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PhloemBowItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/pholemnbow.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PhloemBowItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/combinedarrow.png");
	}
}
