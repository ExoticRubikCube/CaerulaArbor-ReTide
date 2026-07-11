package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.item.LivingArmorstandDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingArmorstandDisplayModel extends GeoModel<LivingArmorstandDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(LivingArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/living_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LivingArmorstandDisplayItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/living_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingArmorstandDisplayItem entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/flamarine_armorstand.png");
	}
}
