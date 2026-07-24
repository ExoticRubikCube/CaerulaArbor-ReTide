package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.FakeOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FakeOffspringModel extends GeoModel<FakeOffspringEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/fakepffspr.png");

	@Override
	public ResourceLocation getAnimationResource(FakeOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/fakeegg.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FakeOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/fakeegg.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FakeOffspringEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FakeOffspringEntity animatable, long instanceId, AnimationState<FakeOffspringEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("ball");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
