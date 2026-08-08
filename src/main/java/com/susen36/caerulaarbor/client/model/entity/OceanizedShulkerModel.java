package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanizedShulkerEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedShulkerModel extends GeoModel<OceanizedShulkerEntity> {
	private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_shulker.png");
	private static final ResourceLocation WHITE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_shulker_white.png");
	private static final ResourceLocation COMPLEX_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_shulker_complex.png");
	private static final ResourceLocation BEDROCK_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_shulker_bedrock.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedShulkerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanized_shulker.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedShulkerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanized_shulker.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedShulkerEntity entity) {
		return switch (entity.getEntityData().get(OceanizedShulkerEntity.DATA_VARIANT)) {
			case 1 -> WHITE_TEXTURE;
			case 2 -> COMPLEX_TEXTURE;
			case 3 -> BEDROCK_TEXTURE;
			default -> DEFAULT_TEXTURE;
		};
	}

	@Override
	public void setCustomAnimations(OceanizedShulkerEntity animatable, long instanceId, AnimationState<OceanizedShulkerEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
		GeoBone shell = getAnimationProcessor().getBone("shell");

		if (shell != null && !animatable.isWalking()){
			Direction dire = animatable.getAttachDirection();
			float pi = (float) Math.PI;
			switch(dire){
                case UP -> {
                    shell.setRotX(0); shell.setRotY(0); shell.setRotZ(0);
                }
                case DOWN -> {
                    shell.setRotX(0); shell.setRotY(0); shell.setRotZ(pi);
                }
                case NORTH -> {
                    shell.setRotX(0); shell.setRotY(0); shell.setRotZ(-pi/2);
                }
                case EAST ->{
                    shell.setRotX(0); shell.setRotY(0); shell.setRotZ(pi/2);
                }
                case WEST -> {
                    shell.setRotX(-pi/2); shell.setRotY(0); shell.setRotZ(-pi/2);
                }
                case SOUTH -> {
                    shell.setRotX(pi/2); shell.setRotY(0); shell.setRotZ(-pi/2);
                }
			}
		}
	}
}