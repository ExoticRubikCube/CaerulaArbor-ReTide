package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.item.CrisisTableDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrisisTableDisplayModel extends GeoModel<CrisisTableDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(CrisisTableDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/crisis_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrisisTableDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/crisis_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrisisTableDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/crisis_table.png");
	}
}