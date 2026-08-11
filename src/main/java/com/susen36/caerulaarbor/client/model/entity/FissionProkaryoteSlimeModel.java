package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.slime.FissionProkaryoteSlimeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class FissionProkaryoteSlimeModel extends GeoModel<FissionProkaryoteSlimeEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/fission_prokaryote.png");

	@Override
	public ResourceLocation getAnimationResource(FissionProkaryoteSlimeEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/fission_prokaryote.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FissionProkaryoteSlimeEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/fission_prokaryote.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FissionProkaryoteSlimeEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FissionProkaryoteSlimeEntity animatable, long instanceId, AnimationState<FissionProkaryoteSlimeEntity> animationState) {
		GeoBone flat = getAnimationProcessor().getBone("flat");
		if (flat != null) {
			boolean isVisible = animatable.getSlimeSize() >= 2.0F;
			flat.setHidden(!isVisible);
			if (isVisible) {
				float ageInTicks = animatable.tickCount + animationState.getPartialTick();
				//10秒1圈
				flat.setRotY(ageInTicks * 0.031415927F);
			}
		}
	}
}