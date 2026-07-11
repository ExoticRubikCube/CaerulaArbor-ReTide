package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedChickenEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedChickenModel extends GeoModel<OceanizedChickenEntity> {
	private static final ResourceLocation ADULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_chicken_adult.png");
	private static final ResourceLocation CHILD_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_chicken_child.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedChickenEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_chicken.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedChickenEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_chicken.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedChickenEntity entity) {
		return entity.getEntityData().get(OceanizedChickenEntity.DATA_IS_CHILD) ? CHILD_TEXTURE : ADULT_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedChickenEntity animatable, long instanceId, AnimationState<OceanizedChickenEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
