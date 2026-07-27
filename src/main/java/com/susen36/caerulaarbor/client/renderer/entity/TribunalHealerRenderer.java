
package com.susen36.caerulaarbor.client.renderer.entity;

import com.susen36.caerulaarbor.entity.TribunalHealerEntity;
import com.susen36.caerulaarbor.client.model.entity.layer.TribunalHealerLayer;
import com.susen36.caerulaarbor.client.model.entity.TribunalHealerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TribunalHealerRenderer extends GeoEntityRenderer<TribunalHealerEntity> {
	public TribunalHealerRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TribunalHealerModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new TribunalHealerLayer(this));
	}

	@Override
	public RenderType getRenderType(TribunalHealerEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected float getDeathMaxRotation(TribunalHealerEntity entityLivingBaseIn) {
		return 0.0F;
	}
}