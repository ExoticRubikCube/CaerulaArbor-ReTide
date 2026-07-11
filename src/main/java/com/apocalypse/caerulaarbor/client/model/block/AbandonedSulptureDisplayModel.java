package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.AbandonedSulptureDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbandonedSulptureDisplayModel extends GeoModel<AbandonedSulptureDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(AbandonedSulptureDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/abandoned_sulpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbandonedSulptureDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/abandoned_sulpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbandonedSulptureDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/theabandoned_sculpture.png");
	}
}
