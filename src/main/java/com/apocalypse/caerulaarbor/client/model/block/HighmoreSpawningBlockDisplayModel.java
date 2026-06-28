package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.HighmoreSpawningBlockDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawningBlockDisplayModel extends GeoModel<HighmoreSpawningBlockDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawningBlockDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawningBlockDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawningBlockDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
