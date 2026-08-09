package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SeaRippleParticle extends TextureSheetParticle {
	public static SeaRippleParticleProvider provider(SpriteSet spriteSet) {
		return new SeaRippleParticleProvider(spriteSet);
	}

	private final float baseQuadSize;
	private final float maxAlpha;
	private final SpriteSet spriteSet;
	private final float angularVelocity;

	protected SeaRippleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		this(world, x, y, z, vx, vy, vz, spriteSet, 0.0588f, 0.2824f, 0.8157f);
	}

	protected SeaRippleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, int color) {
		this(world, x, y, z, vx, vy, vz, spriteSet,
			(float)((color >> 16) & 0xFF) / 255.0F,
			(float)((color >>  8) & 0xFF) / 255.0F,
			(float)( color        & 0xFF) / 255.0F);
		float a = (float)((color >> 24) & 0xFF) / 255.0F;
		if (a > 0.0F) {
			this.setAlpha(a);
		}
	}

	protected SeaRippleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, float r, float g, float b) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.3f, 0.3f);
        float sizeMultiplier = vy > 0.0 ? (float)vy : 1.0F;
        this.quadSize *= (1.12f + this.random.nextFloat() * 0.12f) * sizeMultiplier;
		this.baseQuadSize = this.quadSize;
		this.lifetime = 15 + this.random.nextInt(3);
		this.gravity = 0f;
		this.hasPhysics = false;
		this.friction = 0.96f;
		this.xd = vx * 1.333f;
		this.yd = 0;
		this.zd = vz * 1.333f;
		this.roll = this.random.nextFloat() * ((float) Math.PI * 2F);
		this.oRoll = this.roll;
		this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.02f;
		this.setColor(r, g, b);
		float horizontalSpeed = Mth.sqrt((float) (vx * vx + vz * vz));
		float alphaFactor = Mth.clamp((horizontalSpeed - 0.09f) / 0.05f, 0.0f, 1.0f);
		this.maxAlpha = 0.94f - alphaFactor * 0.22f;
		this.alpha = 0f;
		this.pickSprite(spriteSet);
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public float getQuadSize(float partialTick) {
		float progress = ((float) this.age + partialTick) / (float) this.lifetime;
		float easedProgress = progress * progress * (3.0f - 2.0f * progress);
		return this.baseQuadSize * (0.286f + 1.10f * easedProgress);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.removed) {
			return;
		}
		this.oRoll = this.roll;
		this.roll += this.angularVelocity;
		float progress = (float) this.age / (float) this.lifetime;
		float easedProgress = progress * progress * (3.0f - 2.0f * progress);
		float sizeAlpha = 0.3f + 0.7f * easedProgress;
		float fadeOut = 1.0f - Math.max(0.0f, (progress - 0.9f) / 0.1f);
		this.alpha = this.maxAlpha * sizeAlpha * fadeOut;
		this.setSpriteFromAge(this.spriteSet);
		this.yd = 0;
	}

	public static class SeaRippleParticleProvider implements ParticleProvider<ColorParticleOption> {
		private final SpriteSet spriteSet;

		public SeaRippleParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(ColorParticleOption typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			if (typeIn.getAlpha() > 0.0F) {
				int argb = ((int)(typeIn.getAlpha() * 255.0F) << 24)
					| ((int)(typeIn.getRed()   * 255.0F) << 16)
					| ((int)(typeIn.getGreen() * 255.0F) <<  8)
					|  (int)(typeIn.getBlue()  * 255.0F);
				return new SeaRippleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, argb);
			}
			return new SeaRippleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, typeIn.getRed(), typeIn.getGreen(), typeIn.getBlue());
		}
	}
}