package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.OceanizedEvokerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedEvokerModel extends GeoModel<OceanizedEvokerEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_evoker.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedEvokerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_evoker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedEvokerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_evoker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedEvokerEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedEvokerEntity animatable, long instanceId, AnimationState<OceanizedEvokerEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
