package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.OceanizedCowEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedCowModel extends GeoModel<OceanizedCowEntity> {
	private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanzied_cow.png");
	private static final ResourceLocation TRAILLESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanzied_cow_trailless.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedCowEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_cow.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedCowEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_cow.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedCowEntity entity) {
		return entity.getEntityData().get(OceanizedCowEntity.DATA_SKILL) ? TRAIL_TEXTURE : TRAILLESS_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedCowEntity animatable, long instanceId, AnimationState<OceanizedCowEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("neck");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}