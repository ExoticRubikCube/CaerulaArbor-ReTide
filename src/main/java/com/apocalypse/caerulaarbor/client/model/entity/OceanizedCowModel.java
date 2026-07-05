package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedCowEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedCowModel extends GeoModel<OceanizedCowEntity> {
	private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanzied_cow.png");
	private static final ResourceLocation TRAILLESS_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanzied_cow_trailless.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedCowEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_cow.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedCowEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_cow.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedCowEntity entity) {
		return entity.getEntityData().get(OceanizedCowEntity.DATA_SKILL) ? TRAIL_TEXTURE : TRAILLESS_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedCowEntity animatable, long instanceId, AnimationState<OceanizedCowEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("neck");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
