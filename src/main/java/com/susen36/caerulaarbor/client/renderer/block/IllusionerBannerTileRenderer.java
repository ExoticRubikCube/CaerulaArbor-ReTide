package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.blockentity.IllusionerBannerTileEntity;
import com.susen36.caerulaarbor.client.model.block.IllusionerBannerBlockModel;
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