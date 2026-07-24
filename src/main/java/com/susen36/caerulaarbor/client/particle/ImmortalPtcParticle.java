
package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImmortalPtcParticle extends TextureSheetParticle {
	public static ImmortalPtcParticleProvider provider(SpriteSet spriteSet) {
		return new ImmortalPtcParticleProvider(spriteSet);
	}

	public static class ImmortalPtcParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ImmortalPtcParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ImmortalPtcParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private float angularVelocity;
	private final float angularAcceleration;

	protected ImmortalPtcParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.2f, 0.2f);
		this.quadSize *= 1.75f;
		this.lifetime = Math.max(1, 22 + (this.random.nextInt(8) - 4));
		this.gravity = -0.1f;
		this.hasPhysics = true;
		this.xd = vx * 0.2;
		this.yd = vy * 0.2;
		this.zd = vz * 0.2;
		this.angularVelocity = 0.01f;
		this.angularAcceleration = 0f;
		this.pickSprite(spriteSet);
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
		this.oRoll = this.roll;
		this.roll += this.angularVelocity;
		this.angularVelocity += this.angularAcceleration;
	}
}
