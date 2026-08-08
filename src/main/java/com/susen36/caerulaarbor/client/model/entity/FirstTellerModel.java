package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FirstTellerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FirstTellerModel extends GeoModel<FirstTellerEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/firstlit.png");

	@Override
	public ResourceLocation getAnimationResource(FirstTellerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/firstspeak.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FirstTellerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/firstspeak.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FirstTellerEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FirstTellerEntity animatable, long instanceId, AnimationState<FirstTellerEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}