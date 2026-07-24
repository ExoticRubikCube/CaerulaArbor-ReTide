package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.wither.AbstractOceanizedWitherEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitherEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedWitherModel extends GeoModel<OceanizedWitherEntity> {
	private static final ResourceLocation INVULNERABLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_wither_inv.png");
	private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_wither.png");
	private static final ResourceLocation SHELLED_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_wither_anger.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedWitherEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanzied_wither.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWitherEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanzied_wither.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWitherEntity entity) {
		if (entity.getEntityData().get(AbstractOceanizedWitherEntity.DATA_SHELLED)) {
			return SHELLED_TEXTURE;
		}
		int spawn = entity.getEntityData().get(OceanizedWitherEntity.DATA_SPAWN);
		if (spawn < 40) {
			return INVULNERABLE_TEXTURE;
		}
		if (spawn < 100) {
			return (spawn / 10) % 2 == 0 ? NORMAL_TEXTURE : INVULNERABLE_TEXTURE;
		}
		return NORMAL_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedWitherEntity animatable, long instanceId, AnimationState<OceanizedWitherEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head1");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
