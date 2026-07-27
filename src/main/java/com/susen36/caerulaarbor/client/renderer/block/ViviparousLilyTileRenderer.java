package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.ViviparousLilyTileEntity;
import com.susen36.caerulaarbor.client.model.block.ViviparousLilyBlockModel;
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