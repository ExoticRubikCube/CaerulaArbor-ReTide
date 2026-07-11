package com.apocalypse.caerulaarbor.client.model.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.item.LegendarySpearItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LegendarySpearItemModel extends GeoModel<LegendarySpearItem> {
	@Override
	public ResourceLocation getAnimationResource(LegendarySpearItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/lengdendaryspear.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LegendarySpearItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/lengdendaryspear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LegendarySpearItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/superchitinspear.png");
	}
}
