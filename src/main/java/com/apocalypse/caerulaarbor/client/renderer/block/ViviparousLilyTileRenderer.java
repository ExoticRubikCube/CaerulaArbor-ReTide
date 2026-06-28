package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.entity.ViviparousLilyTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.ViviparousLilyBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ViviparousLilyTileRenderer extends GeoBlockRenderer<ViviparousLilyTileEntity> {
	public ViviparousLilyTileRenderer() {
		super(new ViviparousLilyBlockModel());
	}

	@Override
	public RenderType getRenderType(ViviparousLilyTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
