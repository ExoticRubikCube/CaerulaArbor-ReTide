package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.AbandonedSulptureTileEntity;
import com.apocalypse.caerulaarbor.block.model.AbandonedSulptureBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AbandonedSulptureTileRenderer extends GeoBlockRenderer<AbandonedSulptureTileEntity> {
	public AbandonedSulptureTileRenderer() {
		super(new AbandonedSulptureBlockModel());
	}

	@Override
	public RenderType getRenderType(AbandonedSulptureTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
