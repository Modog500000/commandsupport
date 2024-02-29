package org.modogthedev.commandsupport.markers;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.commandsupport.custom.items.MarkerItem;

import java.util.UUID;

public class MarkerMarker {
    public Vec3 pos;
    public Vec3 vel = Vec3.ZERO;
    public Level level = null;
    public int lifetime = 0;
    public boolean supportCalled = false;
    public MarkerItem.TYPE type;
    public Entity owner;
    public UUID uuid = UUID.randomUUID();
    public double distToPlayer;

    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        ListTag list = tag.getList("marker", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag markerTag = (CompoundTag) t;
            pos = new Vec3(markerTag.getDouble("x"),markerTag.getDouble("y"),markerTag.getDouble("z"));
            vel = new Vec3(markerTag.getDouble("xd"),markerTag.getDouble("yd"),markerTag.getDouble("zd"));
            supportCalled = markerTag.getBoolean("remove");
        }
    }
    protected CompoundTag addAdditionalSaveData() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        CompoundTag marker = new CompoundTag();
        marker.putDouble("x", pos.x);
        marker.putDouble("y", pos.y);
        marker.putDouble("z", pos.z);
        marker.putDouble("xd", vel.x);
        marker.putDouble("yd", vel.y);
        marker.putDouble("zd", vel.z);
        marker.putUUID("uuid", uuid);
        marker.putBoolean("remove", supportCalled);
        list.add(marker);
        tag.put("marker", list);
        return tag;
    }


    public MarkerMarker(Vec3 pos, Level level, MarkerItem.TYPE type, Entity owner) {
        this.pos = pos;
        this.level = level;
        this.type = type;
        this.owner = owner;
        this.vel = owner.getDeltaMovement();
    }
    public MarkerMarker(CompoundTag tag) {
        readAdditionalSaveData(tag);
        if (Minecraft.getInstance() != null) {
            this.level = Minecraft.getInstance().player.level();
        } else {
            this.level = null;
        }
    }
}
