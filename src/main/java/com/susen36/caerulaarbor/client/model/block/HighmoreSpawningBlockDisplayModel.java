package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.item.HighmoreSpawningBlockDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawningBlockDisplayModel extends GeoModel<HighmoreSpawningBlockDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawningBlockDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawningBlockDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawningBlockDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
