package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.MizukiStatueDisplayItem;
import com.apocalypse.caerulaarbor.block.model.MizukiStatueDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MizukiStatueDisplayItemRenderer extends GeoItemRenderer<MizukiStatueDisplayItem> {
	public MizukiStatueDisplayItemRenderer() {
		super(new MizukiStatueDisplayModel());
	}

	@Override
	public RenderType getRenderType(MizukiStatueDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
