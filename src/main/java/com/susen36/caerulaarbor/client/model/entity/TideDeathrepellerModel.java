package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.TideDeathrepellerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TideDeathrepellerModel extends GeoModel<TideDeathrepellerEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/deathrepeller.png");

	@Override
	public ResourceLocation getAnimationResource(TideDeathrepellerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/deathrepeller.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideDeathrepellerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/deathrepeller.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideDeathrepellerEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TideDeathrepellerEntity animatable, long instanceId, AnimationState<TideDeathrepellerEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}