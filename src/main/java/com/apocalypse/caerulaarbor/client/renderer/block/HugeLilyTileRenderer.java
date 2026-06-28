package com.apocalypse.caerulaarbor.client.renderer.block;

import com.apocalypse.caerulaarbor.block.entity.HugeLilyTileEntity;
import com.apocalypse.caerulaarbor.client.model.block.HugeLilyBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HugeLilyTileRenderer extends GeoBlockRenderer<HugeLilyTileEntity> {
	public HugeLilyTileRenderer() {
		super(new HugeLilyBlockModel());
	}

	@Override
	public RenderType getRenderType(HugeLilyTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
