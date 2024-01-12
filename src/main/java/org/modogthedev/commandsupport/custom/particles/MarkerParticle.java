package org.modogthedev.commandsupport.custom.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.commandsupport.markers.MarkerMarker;
import org.modogthedev.commandsupport.util.IRotatingParticleRenderType;

public class MarkerParticle extends TextureSheetParticle {
    MarkerMarker marker;
    protected MarkerParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                            SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 1;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 5;
        this.lifetime = 1;
        this.age = 10;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    protected int getLightColor(float p_107249_) {
        float $$1 = ((float)this.age + p_107249_) / (float)this.lifetime;
        $$1 = Mth.clamp($$1, 0.0F, 1.0F);
        int $$2 = super.getLightColor(p_107249_);
        int $$3 = $$2 & 255;
        int $$4 = $$2 >> 16 & 255;
        $$3 += (int)($$1 * 15.0F * 16.0F);
        if ($$3 > 240) {
            $$3 = 240;
        }

        return $$3 | $$4 << 16;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            MarkerParticle markerParticle = new MarkerParticle(level, x, y, z, this.sprites, dx, dy, dz);
            markerParticle.pickSprite(sprites);
            markerParticle.quadSize = (float) (Math.random()*.1)+1;
            return markerParticle;
        }
    }
}
