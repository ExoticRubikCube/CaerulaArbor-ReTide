package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ApocataEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ApocataModel extends GeoModel<ApocataEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/new_bocchi.png");

	@Override
	public ResourceLocation getAnimationResource(ApocataEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/apocata.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ApocataEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/apocata.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ApocataEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(ApocataEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("realHead");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
