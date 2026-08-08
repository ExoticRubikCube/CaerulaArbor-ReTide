package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FlamarineGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FlamarineGolemModel extends GeoModel<FlamarineGolemEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/flamarine_golem.png");

	@Override
	public ResourceLocation getAnimationResource(FlamarineGolemEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/flamarine_golem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamarineGolemEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/flamarine_golem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamarineGolemEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FlamarineGolemEntity animatable, long instanceId, AnimationState<FlamarineGolemEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("realHead");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}