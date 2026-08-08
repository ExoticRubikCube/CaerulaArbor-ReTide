package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.item.MartusBookItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MartusBookItemModel extends GeoModel<MartusBookItem> {
	@Override
	public ResourceLocation getAnimationResource(MartusBookItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/martus_book.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MartusBookItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/martus_book.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MartusBookItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/item/martus_book.png");
	}
}