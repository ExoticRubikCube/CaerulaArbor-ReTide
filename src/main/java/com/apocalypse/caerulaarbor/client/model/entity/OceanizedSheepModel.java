package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedSheepEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedSheepModel extends GeoModel<OceanizedSheepEntity> {
	private static final ResourceLocation FUR_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_sheep.png");
	private static final ResourceLocation FURLESS_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/oceanized_sheep_furless.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedSheepEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_sheep.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedSheepEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_sheep.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedSheepEntity entity) {
		return entity.getEntityData().get(OceanizedSheepEntity.DATA_FUR) ? FUR_TEXTURE : FURLESS_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedSheepEntity animatable, long instanceId, AnimationState<OceanizedSheepEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("neck");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
