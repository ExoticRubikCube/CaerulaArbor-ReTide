package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;

import com.susen36.caerulaarbor.item.WearableCrownItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WearableCrownModel extends GeoModel<WearableCrownItem> {
	@Override
	public ResourceLocation getAnimationResource(WearableCrownItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/crown.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WearableCrownItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/crown.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WearableCrownItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/crown.png");
	}
}