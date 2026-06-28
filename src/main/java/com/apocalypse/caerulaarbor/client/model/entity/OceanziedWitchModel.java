package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.OceanziedWitchEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanziedWitchModel extends GeoModel<OceanziedWitchEntity> {
	@Override
	public ResourceLocation getAnimationResource(OceanziedWitchEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_witch.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanziedWitchEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_witch.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanziedWitchEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(OceanziedWitchEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
