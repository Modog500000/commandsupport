package org.modogthedev.commandsupport.custom.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import com.mojang.math.Quaternion;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.util.IRotatingParticleRenderType;

public class PlaneParticle extends TextureSheetParticle {
    private final boolean mirror = false;
    protected PlaneParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                            SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 1;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 7;
        this.lifetime = 200;
        this.age = 10;
        this.setSpriteFromAge(spriteSet);
        this.roll = 45;

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }
    @Override
    public void tick() {
        super.tick();
    }


    public Quaternion getCustomRotation(Camera camera, float partialTicks, boolean vert) {
        if (vert) {
            return Vector3f.XP.rotationDegrees(-90);
        } else {
            return Vector3f.YN.rotationDegrees(135);
        }

    }

    @Override
    public void render(VertexConsumer builder, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float originX = (float) (Mth.lerp(partialTicks, xo, x) - cameraPos.x());
        float originY = (float) (Mth.lerp(partialTicks, yo, y) - cameraPos.y());
        float originZ = (float) (Mth.lerp(partialTicks, zo, z) - cameraPos.z());

        Vector3f[] vertices = new Vector3f[] {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float scale = getQuadSize(partialTicks);

        Quaternion rotation = getCustomRotation(camera, partialTicks,true);
        Quaternion rotation1 = getCustomRotation(camera, partialTicks,false);
        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.transform(rotation);
            vertex.transform(rotation1);
            vertex.mul(scale);
            vertex.add(originX, originY, originZ);
        }

        float minU = mirror ? getU1() : getU0();
        float maxU = mirror ? getU0() : getU1();
        float minV = getV0();
        float maxV = getV1();
        int brightness = getLightColor(partialTicks);
        builder.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(brightness).endVertex();
        builder.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(brightness).endVertex();
        builder.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(brightness).endVertex();
        builder.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(brightness).endVertex();
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
            PlaneParticle planeParticle = new PlaneParticle(level, x-300, 300, z-300, this.sprites, dx+5, dy, dz+5);
            planeParticle.pickSprite(sprites);
            planeParticle.quadSize = 15f;
            return planeParticle;
        }
    }
}
