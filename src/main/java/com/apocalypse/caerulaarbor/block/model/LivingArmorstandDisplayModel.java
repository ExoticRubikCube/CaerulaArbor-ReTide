package com.apocalypse.caerulaarbor.block.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.display.LivingArmorstandDisplayItem;

public class LivingArmorstandDisplayModel extends GeoModel<LivingArmorstandDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(LivingArmorstandDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/living_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LivingArmorstandDisplayItem animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/living_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LivingArmorstandDisplayItem entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/flamarine_armorstand.png");
	}
}
