package com.susen36.caerulaarbor.client.model.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCreeperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PocketSeaCreeperPowerLayer extends GeoRenderLayer<PocketSeaCreeperEntity> {
	private static final ResourceLocation POWER_LOCATION = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/pocket_sea_creeper_armor.png");

	public PocketSeaCreeperPowerLayer(GeoRenderer<PocketSeaCreeperEntity> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(PoseStack poseStack, PocketSeaCreeperEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		if (!animatable.charged()) {
			return;
		}

		float f = (float) animatable.tickCount + partialTick;
		RenderType energySwirlType = RenderType.energySwirl(POWER_LOCATION, this.xOffset(f) % 0.5F, f * 0.01F % 0.5F);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, energySwirlType, bufferSource.getBuffer(energySwirlType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 0x80FFFFFF);
	}

	private float xOffset(float tickCount) {
		return tickCount * 0.01F;
	}
}