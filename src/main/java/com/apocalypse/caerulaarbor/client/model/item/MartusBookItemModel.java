package com.apocalypse.caerulaarbor.client.model.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.item.MartusBookItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MartusBookItemModel extends GeoModel<MartusBookItem> {
	@Override
	public ResourceLocation getAnimationResource(MartusBookItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/martus_book.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MartusBookItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/martus_book.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MartusBookItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/item/martus_book.png");
	}
}
