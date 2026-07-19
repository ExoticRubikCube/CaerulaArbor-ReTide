
package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.client.model.entity.OceanizedEnderinaModel;
import com.apocalypse.caerulaarbor.client.model.entity.layer.OceanizedEnderinaLayer;
import com.apocalypse.caerulaarbor.entity.OceanizedEnderinaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OceanizedEnderinaRenderer extends GeoEntityRenderer<OceanizedEnderinaEntity> {
	public OceanizedEnderinaRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new OceanizedEnderinaModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new OceanizedEnderinaLayer(this));
	}

	@Override
	public RenderType getRenderType(OceanizedEnderinaEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		// 当前自定义 core shader 替换整个实体渲染类型，第三方光影包不保证兼容；未来应让主体回归原版 RenderType，并将效果迁移到独立渲染层。
		return OceanizedEnderinaRenderType.get(animatable, partialTick);
	}

	@Override
	public void renderRecursively(PoseStack stack, OceanizedEnderinaEntity animatable, GeoBone bone, RenderType type, MultiBufferSource buffer, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		if (bone.getName().equals("EyeFloat")) {
			// EyeFloat 及其子骨骼使用原版 RenderType，避免自定义 shader 覆盖。
			type = RenderType.entityTranslucent(getTextureLocation(animatable));
			bufferIn = buffer.getBuffer(type);
		}
		super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void preRender(PoseStack poseStack, OceanizedEnderinaEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(OceanizedEnderinaEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
