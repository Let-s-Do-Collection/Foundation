package net.satisfy.foundation.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/** Fly buzzing in a wobbly circle around its spawn point. Positive x speed = base lifetime in ticks, otherwise 60. */
public class FlyParticle extends TextureSheetParticle {
    private static final int DEFAULT_LIFETIME = 60;

    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final float radius;
    private final float speed;
    private final float phase;

    protected FlyParticle(ClientLevel level, double x, double y, double z, int baseLifetime, SpriteSet sprites) {
        super(level, x, y, z);
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.lifetime = baseLifetime + this.random.nextInt(60);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize = 0.035F + this.random.nextFloat() * 0.015F;
        this.radius = 0.15F + this.random.nextFloat() * 0.2F;
        this.speed = (0.25F + this.random.nextFloat() * 0.2F) * (this.random.nextBoolean() ? 1.0F : -1.0F);
        this.phase = this.random.nextFloat() * Mth.TWO_PI;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float angle = this.phase + this.age * this.speed;
        double jitter = (this.random.nextDouble() - 0.5) * 0.05;
        this.setPos(
                this.centerX + Mth.cos(angle) * this.radius + jitter,
                this.centerY + Mth.sin(this.age * 0.21F + this.phase) * 0.12 + jitter,
                this.centerZ + Mth.sin(angle) * this.radius + jitter);
        this.alpha = Math.min(1.0F, Math.min(this.age, this.lifetime - this.age) / 8.0F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            int baseLifetime = xSpeed > 0 ? (int) xSpeed : DEFAULT_LIFETIME;
            return new FlyParticle(level, x, y, z, baseLifetime, this.sprites);
        }
    }
}
