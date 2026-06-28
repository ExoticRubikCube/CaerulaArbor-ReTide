package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.CrisisTableDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrisisTableDisplayModel extends GeoModel<CrisisTableDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(CrisisTableDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/crisis_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrisisTableDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/crisis_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrisisTableDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/crisis_table.png");
	}
}
