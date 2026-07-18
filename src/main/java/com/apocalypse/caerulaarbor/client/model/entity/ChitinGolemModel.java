package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ChitinGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ChitinGolemModel extends GeoModel<ChitinGolemEntity> {
	private static final ResourceLocation HEALTHY_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chitin_golem_0.png");
	private static final ResourceLocation DAMAGED_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chitin_golem_1.png");
	private static final ResourceLocation HEAVY_DAMAGED_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chitin_golem_2.png");
	private static final ResourceLocation CRITICAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chitin_golem_3.png");

	@Override
	public ResourceLocation getAnimationResource(ChitinGolemEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chitgolem.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChitinGolemEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chitgolem.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChitinGolemEntity entity) {
		float healthRatio = entity.getHealth() / entity.getMaxHealth();
		if (healthRatio < 0.25F) {
			return CRITICAL_TEXTURE;
		}
		if (healthRatio < 0.5F) {
			return HEAVY_DAMAGED_TEXTURE;
		}
		if (healthRatio < 0.75F) {
			return DAMAGED_TEXTURE;
		}
		return HEALTHY_TEXTURE;
	}

	@Override
	public void setCustomAnimations(ChitinGolemEntity animatable, long instanceId, AnimationState<ChitinGolemEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
