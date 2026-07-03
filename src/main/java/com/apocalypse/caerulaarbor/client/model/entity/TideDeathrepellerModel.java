package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TideDeathrepellerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TideDeathrepellerModel extends GeoModel<TideDeathrepellerEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/deathrepellertexture.png");

	@Override
	public ResourceLocation getAnimationResource(TideDeathrepellerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/deathrepeller.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideDeathrepellerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/deathrepeller.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideDeathrepellerEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TideDeathrepellerEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
