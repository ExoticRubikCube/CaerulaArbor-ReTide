package com.apocalypse.caerulaarbor.client.renderer.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedEnderinaEntity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

// 当前为增强效果替换主体渲染，未来应迁移为附加渲染层。
public final class OceanizedEnderinaRenderType {
	private static final ResourceLocation TEXTURE = CaerulaArborMod.ModLoc("textures/entities/oceanized_enderina.png");
	private static final Map<Integer, RenderType> RENDER_TYPES = new HashMap<>();
	private static ShaderInstance shader;

	private OceanizedEnderinaRenderType() {
	}

	public static void setShader(ShaderInstance shaderInstance) {
		shader = shaderInstance;
		RENDER_TYPES.clear();
	}

	public static RenderType get(OceanizedEnderinaEntity entity, float partialTick) {
		if (shader != null) {
			int deathStep = Math.min(40, Math.max(0, (int) (entity.getDeathTextureTick() + partialTick)));
			int reviveTick = entity.getEntityData().get(OceanizedEnderinaEntity.DATA_REVIVE_TICK);
			int phase = entity.getEntityData().get(OceanizedEnderinaEntity.DATA_PHASE);
			boolean noiseEnabled = phase > 0 && (entity.getHealth() >= entity.getMaxHealth() || reviveTick < 100);
			int key = deathStep * 2 + (noiseEnabled ? 1 : 0);
			return RENDER_TYPES.computeIfAbsent(key, unused -> RenderType.create("oceanized_enderina_" + key, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					RenderType.CompositeState.builder()
							.setShaderState(new RenderStateShard.ShaderStateShard(() -> {
								shader.getUniform("DissolveProgress").set(deathStep / 40.0F);
								shader.getUniform("NoiseEnabled").set(noiseEnabled ? 1.0F : 0.0F);
								return shader;
							}))
							.setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
							.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
							.setCullState(RenderType.NO_CULL)
							.setLightmapState(RenderType.LIGHTMAP)
							.setOverlayState(RenderType.OVERLAY)
							.createCompositeState(true)));
		}
		return RenderType.entityTranslucent(TEXTURE);
	}
}