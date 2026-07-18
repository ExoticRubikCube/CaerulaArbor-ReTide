package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedRavagerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedRavagerModel extends GeoModel<OceanizedRavagerEntity> {
	@Override
	public ResourceLocation getAnimationResource(OceanizedRavagerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_ravager.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedRavagerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_ravager.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedRavagerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ravager.png");
	}

	@Override
	public void setCustomAnimations(OceanizedRavagerEntity animatable, long instanceId, AnimationState<OceanizedRavagerEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("outerhead");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
