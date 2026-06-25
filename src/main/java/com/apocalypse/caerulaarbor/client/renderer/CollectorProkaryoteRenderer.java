
package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.entity.CollectorProkaryoteEntity;
import com.apocalypse.caerulaarbor.client.model.layer.CollectorProkaryoteLayer;
import com.apocalypse.caerulaarbor.client.model.CollectorProkaryoteModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CollectorProkaryoteRenderer extends GeoEntityRenderer<CollectorProkaryoteEntity> {
	public CollectorProkaryoteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new CollectorProkaryoteModel());
		this.shadowRadius = 0.3f;
		this.addRenderLayer(new CollectorProkaryoteLayer(this));
	}

	@Override
	public RenderType getRenderType(CollectorProkaryoteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, CollectorProkaryoteEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 0.6f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
