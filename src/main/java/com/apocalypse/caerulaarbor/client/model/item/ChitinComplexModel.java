package com.apocalypse.caerulaarbor.client.model.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.item.ChitinComplexItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChitinComplexModel extends GeoModel<ChitinComplexItem> {
	@Override
	public ResourceLocation getAnimationResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/complex_chitin.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/complex_chitin.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChitinComplexItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/complex_chitin_armor.png");
	}
}
