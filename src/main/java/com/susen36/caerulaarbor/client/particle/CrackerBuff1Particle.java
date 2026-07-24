package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrackerBuff1Particle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    protected CrackerBuff1Particle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.lifetime = Math.max(1, 28 + (this.random.nextInt(14) - 7));
        this.gravity = -0.2f;
        this.hasPhysics = true;
        this.xd = vx * 0.1;
        this.yd = vy * 0.1;
        this.zd = vz * 0.1;
        this.pickSprite(spriteSet);
    }

    public static CrackerBuff1ParticleProvider provider(SpriteSet spriteSet) {
        return new CrackerBuff1ParticleProvider(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class CrackerBuff1ParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public CrackerBuff1ParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new CrackerBuff1Particle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
