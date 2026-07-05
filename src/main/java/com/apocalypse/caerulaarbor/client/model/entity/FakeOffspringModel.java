package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.FakeOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FakeOffspringModel extends GeoModel<FakeOffspringEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/fakepffspr.png");

	@Override
	public ResourceLocation getAnimationResource(FakeOffspringEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/fakeegg.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FakeOffspringEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/fakeegg.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FakeOffspringEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FakeOffspringEntity animatable, long instanceId, AnimationState<FakeOffspringEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("ball");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
