package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.ChitinComplexItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChitinComplexModel extends GeoModel<ChitinComplexItem> {
	@Override
	public ResourceLocation getAnimationResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/complex_chitin.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/complex_chitin.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/complex_chitin_armor.png");
	}
}