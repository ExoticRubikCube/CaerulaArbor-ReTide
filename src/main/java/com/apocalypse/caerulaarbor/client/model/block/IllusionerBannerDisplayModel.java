package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.IllusionerBannerDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IllusionerBannerDisplayModel extends GeoModel<IllusionerBannerDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(IllusionerBannerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IllusionerBannerDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IllusionerBannerDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
	}
}
