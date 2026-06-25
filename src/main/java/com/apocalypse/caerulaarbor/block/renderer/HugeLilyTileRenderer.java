package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.HugeLilyTileEntity;
import com.apocalypse.caerulaarbor.block.model.HugeLilyBlockModel;
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
