package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = CaerulaArborMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class OceanizedEnderinaShaders {
	private OceanizedEnderinaShaders() {
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException {
		ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), CaerulaArborMod.ModLoc("oceanized_enderina"), DefaultVertexFormat.NEW_ENTITY);
		event.registerShader(shader, OceanizedEnderinaRenderType::setShader);
	}
}