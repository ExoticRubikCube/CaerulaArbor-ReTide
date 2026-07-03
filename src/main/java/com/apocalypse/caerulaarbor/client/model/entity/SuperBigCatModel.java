package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SuperBigCatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SuperBigCatModel extends GeoModel<SuperBigCatEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/super_big_cat.png");

	@Override
	public ResourceLocation getAnimationResource(SuperBigCatEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_cat.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SuperBigCatEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_cat.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SuperBigCatEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(SuperBigCatEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
