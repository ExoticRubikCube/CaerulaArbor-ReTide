package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedIllusionerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedIllusionerModel extends GeoModel<OceanizedIllusionerEntity> {
	private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_illusioner.png");
	private static final ResourceLocation BROKEN_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_illusioner_broken.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedIllusionerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_illusioner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedIllusionerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_illusioner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedIllusionerEntity entity) {
		return entity.getHealth() < entity.getMaxHealth() * 0.5F ? BROKEN_TEXTURE : NORMAL_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedIllusionerEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
