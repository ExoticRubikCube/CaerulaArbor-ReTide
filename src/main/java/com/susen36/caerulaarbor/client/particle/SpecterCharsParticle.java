
package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecterCharsParticle extends TextureSheetParticle {
	public static SpecterCharsParticleProvider provider(SpriteSet spriteSet) {
		return new SpecterCharsParticleProvider(spriteSet);
	}

	public static class SpecterCharsParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public SpecterCharsParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new SpecterCharsParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected SpecterCharsParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.2f, 0.2f);
		this.quadSize *= 2f;
		this.lifetime = Math.max(1, 27 + (this.random.nextInt(6) - 3));
		this.gravity = -0.15f;
		this.hasPhysics = true;
		this.xd = vx * 0.5;
		this.yd = vy * 0.5;
		this.zd = vz * 0.5;
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			this.setSprite(this.spriteSet.get((this.age / 5) % 6 + 1, 6));
		}
	}
}
