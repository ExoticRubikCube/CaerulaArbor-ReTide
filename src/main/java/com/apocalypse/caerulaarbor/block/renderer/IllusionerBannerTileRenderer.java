package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.entity.IllusionerBannerTileEntity;
import com.apocalypse.caerulaarbor.block.model.IllusionerBannerBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class IllusionerBannerTileRenderer extends GeoBlockRenderer<IllusionerBannerTileEntity> {
	public IllusionerBannerTileRenderer() {
		super(new IllusionerBannerBlockModel());
	}

	@Override
	public RenderType getRenderType(IllusionerBannerTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
