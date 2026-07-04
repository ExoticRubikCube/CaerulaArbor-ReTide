package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizeRabbitEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizeRabbitModel extends GeoModel<OceanizeRabbitEntity> {
	private static final ResourceLocation BLOODY_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_rabbit_bloody.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizeRabbitEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_rabbit.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizeRabbitEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_rabbit.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizeRabbitEntity entity) {
		int variant = entity.getEntityData().get(OceanizeRabbitEntity.DATA_VARIANT);
		if (variant == 5) {
			return BLOODY_TEXTURE;
		}
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_rabbit_" + variant + ".png");
	}

	@Override
	public void setCustomAnimations(OceanizeRabbitEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
