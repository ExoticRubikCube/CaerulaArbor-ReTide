package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.item.CentrifugerDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugerDisplayModel extends GeoModel<CentrifugerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(CentrifugerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/centrifuger.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CentrifugerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/centrifuger.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CentrifugerDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/centrifuger.png");
	}
}
