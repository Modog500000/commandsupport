package org.modogthedev.commandsupport.markers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.core.jmx.Server;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.core.ModParticles;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.custom.items.MarkerItem;
import org.modogthedev.commandsupport.networking.Messages;
import org.modogthedev.commandsupport.networking.packets.PacketSyncMarkers;

import java.util.*;

public class MarkerHandeler {
    public static List<MarkerMarker> markers = new ArrayList<>();
    public static List<MarkerMarker> clientMarkers = new ArrayList<>();
    public static List<SupportMarker> inboundSupport = new ArrayList<>();
    public static List<SupportMarker> inboundSupportRemove = new ArrayList<>();
    public static List<MarkerMarker> toHandel = new ArrayList<>();


    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        for (SupportMarker marker : inboundSupportRemove) {
            inboundSupport.remove(marker);
        }
        for (MarkerMarker marker : markers) {
            BlockHitResult blockHitResult = marker.level.clip(new ClipContext(marker.pos, marker.pos.add(marker.vel), ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.WATER, null));
            if (blockHitResult.getType() == HitResult.Type.MISS) {
                marker.pos = marker.pos.add(marker.vel);
                marker.vel = marker.vel.add(new Vec3(0, -.01, 0));
                marker.vel = marker.vel.multiply(new Vec3(0.99, 1, 0.99));
            } else {
                marker.vel = Vec3.ZERO;
            }
            marker.lifetime++;
        }
        for (SupportMarker marker : inboundSupport) {
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
            if (marker.lifespan == 0 && marker.delay == 200 && !marker.hasParticle) {
                displayPlaneParticle(marker, ModParticles.PLANE_PARTICLE.get());
                marker.hasParticle = true;
                if (marker.type == MarkerItem.TYPE.NUKE) {
                    marker.level.playSound(null, marker.pos.x, marker.pos.y, marker.pos.z, ModSounds.ALARM.get(), SoundSource.PLAYERS, 25f, 1);
                }
            }

        }
    }

    @SubscribeEvent
    public static void tickLevel(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        if (!level.isClientSide) {
            MinecraftServer minecraftServer = level.getServer();
            for (Player player : level.players()) {
                List<MarkerMarker> toSend = new ArrayList<>();
                for (MarkerMarker marker : markers) {
                    if (marker.pos.distanceTo(player.position()) < 100) {
                        toSend.add(marker);
                    }
                }
                for (MarkerMarker marker : toSend) {
                    PacketSyncMarkers packet = new PacketSyncMarkers(marker.addAdditionalSaveData());
                    Messages.sendToPlayer(packet, (ServerPlayer) player);
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientTick(TickEvent.ClientTickEvent event) {
        List<MarkerMarker> toAdd = new ArrayList<>();
        if (!toHandel.isEmpty()) {
            for (MarkerMarker marker : toHandel) {
                if (!clientMarkers.isEmpty()) {
                    boolean found = false;
                    for (MarkerMarker marker1 : clientMarkers) {
                        if (marker1.uuid == marker.uuid) {
                            marker1.pos = marker.pos;
                            marker1.vel = marker.vel;
                            marker1.lifetime = 0;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        toAdd.add(marker);
                    }
                } else  {
                    toAdd.add(marker);
                }
            }
        }
        toHandel.clear();
        clientMarkers.addAll(toAdd);
        List<MarkerMarker> toRemove = new ArrayList<>();
        for (MarkerMarker marker : clientMarkers) {
            BlockHitResult blockHitResult = marker.level.clip(new ClipContext(marker.pos, marker.pos.add(marker.vel), ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.WATER, null));
            if (blockHitResult.getType() == HitResult.Type.MISS) {
                marker.pos = marker.pos.add(marker.vel);
                marker.vel = marker.vel.add(new Vec3(0, -.01, 0));
                marker.vel = marker.vel.multiply(new Vec3(0.99, 1, 0.99));
            } else {
                marker.vel = Vec3.ZERO;
            }
            marker.lifetime++;
            if (marker.lifetime >20) {
                toRemove.add(marker);
            }
        }
        clientMarkers.removeAll(toRemove);
        toRemove.clear();
        for (MarkerMarker marker : clientMarkers) {
            displayMarkerParticle(marker, ModParticles.MARKER_PARTICLE.get());
        }
        for (
                SupportMarker marker : inboundSupport) {
            if (marker.lifespan == 0 && marker.delay == 200) {

            }
            if (marker.lifespan == 70) {

            }
        }
    }

    public static void addMarker(Vec3 pos, Level level, MarkerItem.TYPE type, Entity owner) {
        markers.add(new MarkerMarker(pos, level, type, owner));
    }

    @OnlyIn(Dist.CLIENT)
    public static void displayMarkerParticle(MarkerMarker marker, ParticleOptions particleOptions) {
        Minecraft.getInstance().level.addParticle(particleOptions, marker.pos.x, marker.pos.y, marker.pos.z, 0, 0, 0);
    }

    public static void displayPlaneParticle(SupportMarker marker, ParticleOptions particleOptions) {
        ServerLevel level = (ServerLevel) marker.level;
        for (ServerPlayer serverplayer : level.players()) {
            level.sendParticles(serverplayer, particleOptions, true, marker.pos.x, marker.pos.y, marker.pos.z, 1, 0, 0, 0, 0);
        }

    }

    public static Vec3 getPosForDeploy(Vec3 pos, Level level) {
        BlockHitResult blockHitResult = level.clip(new ClipContext(new Vec3(pos.x, 400, pos.z), pos, ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.WATER, null));
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return pos;
        } else {
            return blockHitResult.getLocation().add(0, 1, 0);
        }
    }

    public static void supportDeployEvent(Level level, Vec3 prepos, MarkerItem.TYPE type, Entity owner) {
        if (!level.isClientSide) {
            Vec3 pos = getPosForDeploy(prepos, level);
            switch (type) {
                case SUPPLY -> {
                    LootTable table = Objects.requireNonNull(owner.getServer()).getLootData().getLootTable(((new ResourceLocation("commandsupport:crate"))));
                    BlockState chest = Blocks.CHEST.defaultBlockState();
                    BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
                    level.setBlock(blockPos, chest, 0);
                    if (level.getBlockEntity(blockPos) instanceof ChestBlockEntity) {
                        table.fill((Container) level.getBlockEntity(blockPos), new LootParams.Builder((ServerLevel) level).create(new LootContextParamSet.Builder().optional(new LootContextParam<>(new ResourceLocation("commandsupport:crate"))).build()), (long) (Math.random()*1000));
                    }
                    level.sendBlockUpdated(blockPos, level.getBlockState(blockPos), level.getBlockState(blockPos), Block.UPDATE_ALL);
                }
                case AIRSTRIKE -> {
                    level.explode(owner, pos.x, pos.y, pos.z, 10.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 2, pos.z, 8.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 4, pos.z, 8.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 6, pos.z, 8.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 8, pos.z, 6.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 10, pos.z, 6.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y - 12, pos.z, 6.0f, Level.ExplosionInteraction.TNT);
                }
                case NUKE -> {
                    level.explode(owner, pos.x, pos.y, pos.z, 120f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x + 20, pos.y, pos.z, 60.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x - 20, pos.y, pos.z, 60.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x + 20, pos.y, pos.z + 20, 60.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x - 20, pos.y, pos.z - 20, 60.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y, pos.z + 20, 60.0f, Level.ExplosionInteraction.TNT);
                    level.explode(owner, pos.x, pos.y, pos.z - 20, 60.0f, Level.ExplosionInteraction.TNT);
                }
            }
        }
    }

    public static boolean callSupportEvent(Vec3 pos, Level level, int radius) {
        if (!markers.isEmpty()) {
            List<MarkerMarker> explodeable = new ArrayList<>();
            for (MarkerMarker marker : markers) {
                marker.distToPlayer = marker.pos.distanceTo(pos);
            }
            explodeable.addAll(markers);
            explodeable.sort((o1, o2) -> (int) (o1.distToPlayer - o2.distToPlayer));
            for (MarkerMarker marker : markers) {
                if (marker.level == level) {
                    if (marker.pos.distanceTo(pos) <= radius && !marker.supportCalled) {

                        marker.supportCalled = true;
                        inboundSupport.add(new SupportMarker(marker.pos, level, marker, marker.type, marker.owner));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<MarkerMarker> getMarkers() {
        return markers;
    }

    public List<SupportMarker> getSupportMarkers() {
        return inboundSupport;
    }

    public void setMarkers(List<MarkerMarker> list) {
        markers.clear();
        markers.addAll(list);
    }
}
