package com.apocalypse.caerulaarbor.block.renderer;

import com.apocalypse.caerulaarbor.block.display.CrisisTableDisplayItem;
import com.apocalypse.caerulaarbor.block.model.CrisisTableDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrisisTableDisplayItemRenderer extends GeoItemRenderer<CrisisTableDisplayItem> {
	public CrisisTableDisplayItemRenderer() {
		super(new CrisisTableDisplayModel());
	}

	@Override
	public RenderType getRenderType(CrisisTableDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
