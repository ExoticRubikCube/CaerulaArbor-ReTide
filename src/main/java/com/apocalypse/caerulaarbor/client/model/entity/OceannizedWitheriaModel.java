package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.wither.AbstractOceanizedWitherEntity;
import com.apocalypse.caerulaarbor.entity.wither.OceannizedWitheriaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceannizedWitheriaModel extends GeoModel<OceannizedWitheriaEntity> {
	private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_witheria.png");
	private static final ResourceLocation SHELLED_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_witheria_anger.png");

	@Override
	public ResourceLocation getAnimationResource(OceannizedWitheriaEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanzied_witheria.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceannizedWitheriaEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanzied_witheria.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceannizedWitheriaEntity entity) {
		return entity.getEntityData().get(AbstractOceanizedWitherEntity.DATA_SHELLED) ? SHELLED_TEXTURE : DEFAULT_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceannizedWitheriaEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
