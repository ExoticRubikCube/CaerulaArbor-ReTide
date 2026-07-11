package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TideChimeraEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TideChimeraModel extends GeoModel<TideChimeraEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/general_seaborns.png");

	@Override
	public ResourceLocation getAnimationResource(TideChimeraEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/super_apocata.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideChimeraEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/super_apocata.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideChimeraEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TideChimeraEntity animatable, long instanceId, AnimationState<TideChimeraEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("forHead");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
