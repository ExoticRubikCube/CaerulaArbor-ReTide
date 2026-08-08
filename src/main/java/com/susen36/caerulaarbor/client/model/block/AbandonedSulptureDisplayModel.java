package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.item.AbandonedSulptureDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbandonedSulptureDisplayModel extends GeoModel<AbandonedSulptureDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(AbandonedSulptureDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/abandoned_sulpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbandonedSulptureDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/abandoned_sulpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbandonedSulptureDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/theabandoned_sculpture.png");
	}
}