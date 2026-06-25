package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.CentrifugerTileEntity;
import com.apocalypse.caerulaarbor.block.model.CentrifugerBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CentrifugerTileRenderer extends GeoBlockRenderer<CentrifugerTileEntity> {
	public CentrifugerTileRenderer() {
		super(new CentrifugerBlockModel());
	}

	@Override
	public RenderType getRenderType(CentrifugerTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
