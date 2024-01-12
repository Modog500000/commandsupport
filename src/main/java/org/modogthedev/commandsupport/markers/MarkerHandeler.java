package org.modogthedev.commandsupport.markers;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.core.ModParticles;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.custom.items.MarkerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarkerHandeler {
    public static List<MarkerMarker> markers = new ArrayList<>();
    public static List<SupportMarker> inboundSupport = new ArrayList<>();
    public static List<SupportMarker> inboundSupportRemove = new ArrayList<>();


    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        for (SupportMarker marker: inboundSupportRemove) {
            inboundSupport.remove(marker);
        }
        for (MarkerMarker marker: markers) {
            BlockHitResult blockHitResult = marker.level.clip(new ClipContext(marker.pos,marker.pos.add(marker.vel), ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.WATER, null));
            if (blockHitResult.getType() == HitResult.Type.MISS) {
                marker.pos = marker.pos.add(marker.vel);
                marker.vel = marker.vel.add(new Vec3(0,-.01,0));
                marker.vel = marker.vel.multiply(new Vec3(0.99,1,0.99));
            } else {
                marker.vel = Vec3.ZERO;
            }
            marker.lifetime++;
        }
        for (SupportMarker marker: inboundSupport) {
            if (marker.delay >= 200) {
                if (marker.lifespan >= 120 && !marker.hasDeployed) {
                    supportDeployEvent(marker.level, marker.pos, marker.type, marker.owner);
                    marker.hasDeployed = true;
                    markers.remove(marker.marker);
                    inboundSupportRemove.add(marker);
                }
                marker.lifespan++;
            } else {
                marker.delay++;
            }
        }
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientTick(TickEvent.ClientTickEvent event) {
        for (MarkerMarker marker: markers) {
            displayMarkerParticle(marker, ModParticles.MARKER_PARTICLE.get());
        }
        for (SupportMarker marker: inboundSupport) {
            if (marker.lifespan == 0 && marker.delay == 200) {
                displayPlaneParticle(marker, ModParticles.PLANE_PARTICLE.get());
            }
            if (marker.lifespan == 0 && marker.delay == 200) {

            }
        }
    }

    public static void addMarker(Vec3 pos, Level level, MarkerItem.TYPE type, Entity owner) {
        markers.add(new MarkerMarker(pos,level, type, owner));
    }
    @OnlyIn(Dist.CLIENT)
    public static void displayMarkerParticle(MarkerMarker marker, ParticleOptions particleOptions) {
        Minecraft.getInstance().level.addParticle(particleOptions, marker.pos.x,marker.pos.y,marker.pos.z, 0, 0, 0);
    }
    @OnlyIn(Dist.CLIENT)
    
    public static void displayPlaneParticle(SupportMarker marker, ParticleOptions particleOptions) {
        Minecraft.getInstance().level.addParticle(particleOptions, marker.pos.x,marker.pos.y,marker.pos.z, 0, 0, 0);
    }
    public static void supportDeployEvent(Level level, Vec3 pos, MarkerItem.TYPE type, Entity owner) {
        if (!level.isClientSide) {
            switch (type) {
                case SUPPLY -> {
                    LootTable table = Objects.requireNonNull(owner.getServer()).getLootTables().get((new ResourceLocation("commandsupport:crate")));
                    BlockState chest = Blocks.CHEST.defaultBlockState();
                    BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
                    level.setBlock(blockPos, chest, 0);
                    LootContext.Builder builder = (new LootContext.Builder((ServerLevel) level))
                            .withParameter(LootContextParams.ORIGIN, pos);
                    if (level.getBlockEntity(blockPos) instanceof ChestBlockEntity) {
                        table.fill((Container) level.getBlockEntity(blockPos), builder.create(LootContextParamSets.CHEST));
                    }
                    level.sendBlockUpdated(blockPos,level.getBlockState(blockPos),level.getBlockState(blockPos), Block.UPDATE_ALL);
                    Commandsupport.LOGGER.info("Place!");
                }
                case AIRSTRIKE -> {
                    level.explode(owner, pos.x, pos.y, pos.z, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 2, pos.z, 8.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 4, pos.z, 8.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 6, pos.z, 8.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 8, pos.z, 6.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 10, pos.z, 6.0f,Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y - 12, pos.z, 6.0f,Explosion.BlockInteraction.BREAK);
                }
                case NUKE -> {
                    level.explode(owner, pos.x, pos.y, pos.z, 40.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x+10, pos.y, pos.z, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x-10, pos.y, pos.z, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x+10, pos.y, pos.z+10, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x-10, pos.y, pos.z-10, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y, pos.z+10, 10.0f, Explosion.BlockInteraction.BREAK);
                    level.explode(owner, pos.x, pos.y, pos.z-10, 10.0f, Explosion.BlockInteraction.BREAK);
                }
            }
        }
    }
    public static boolean callSupportEvent(Vec3 pos, Level level, int radius) {
        for (MarkerMarker marker: markers) {
            if (marker.level == level) {
                if (marker.pos.distanceTo(pos) <= radius && !marker.supportCalled) {
                    if (marker.type == MarkerItem.TYPE.NUKE) {
                        level.playSound(null,marker.pos.x,marker.pos.y,marker.pos.z, ModSounds.ALARM.get(), SoundSource.PLAYERS, 25f ,1);
                    }
                    inboundSupport.add(new SupportMarker(marker.pos,level, marker, marker.type, marker.owner));
                    marker.supportCalled = true;
                    return true;
                }
            }
        }
        return false;
    }
}
