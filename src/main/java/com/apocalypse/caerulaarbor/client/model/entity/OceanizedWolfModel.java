package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedWolfEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedWolfModel extends GeoModel<OceanizedWolfEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_wolf.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedWolfEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_wolf.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWolfEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_wolf.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWolfEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedWolfEntity animatable, long instanceId, AnimationState<OceanizedWolfEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
