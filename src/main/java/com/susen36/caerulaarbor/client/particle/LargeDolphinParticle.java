package com.susen36.caerulaarbor.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeDolphinParticle extends SuspendedTownParticle {
    private LargeDolphinParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.pickSprite(spriteSet);
        this.setColor(0.3F, 0.5F, 1.0F);
        this.setAlpha(1.0F - this.random.nextFloat() * 0.7F - 0.15F);
        this.lifetime = 15 + this.random.nextInt(10);
        this.quadSize *= 1.8F;
    }

    public static LargeDolphinParticleProvider provider(SpriteSet spriteSet) {
        return new LargeDolphinParticleProvider(spriteSet);
    }

    public static class LargeDolphinParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public LargeDolphinParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LargeDolphinParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
