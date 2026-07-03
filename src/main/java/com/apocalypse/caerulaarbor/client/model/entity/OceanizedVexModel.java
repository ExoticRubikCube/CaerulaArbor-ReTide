package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedVexEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedVexModel extends GeoModel<OceanizedVexEntity> {
	private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_vex.png");
	private static final ResourceLocation CHARGING_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_vex_charging.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedVexEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_vex.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedVexEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_vex.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedVexEntity entity) {
		return entity.isAggressive() ? CHARGING_TEXTURE : NORMAL_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedVexEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
