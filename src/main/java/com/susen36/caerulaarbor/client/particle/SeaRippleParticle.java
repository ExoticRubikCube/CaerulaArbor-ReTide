package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SeaRippleParticle extends TextureSheetParticle {
	public static SeaRippleParticleProvider provider(SpriteSet spriteSet) {
		return new SeaRippleParticleProvider(spriteSet);
	}

	public static class SeaRippleParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public SeaRippleParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new SeaRippleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final float baseQuadSize;
	private final float maxAlpha;
	private final SpriteSet spriteSet;
	private final float angularVelocity;

	protected SeaRippleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.3f, 0.3f);
		this.quadSize *= 1.12f + this.random.nextFloat() * 0.12f;
		this.baseQuadSize = this.quadSize;
		this.lifetime = 20 + this.random.nextInt(6);
		this.gravity = 0f;
		this.hasPhysics = false;
		this.friction = 0.96f;
		this.xd = vx;
		this.yd = 0;
		this.zd = vz;
		this.roll = this.random.nextFloat() * ((float) Math.PI * 2F);
		this.oRoll = this.roll;
		this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.02f;
		this.rCol = 0.05f;
		this.gCol = 0.15f;
		this.bCol = 0.72f;
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
		return this.baseQuadSize * (0.26f + 1.82f * easedProgress);
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
}
