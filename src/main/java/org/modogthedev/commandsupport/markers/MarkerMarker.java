package org.modogthedev.commandsupport.markers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.commandsupport.custom.items.MarkerItem;

public class MarkerMarker {
    public Vec3 pos;
    public Vec3 vel = Vec3.ZERO;
    public final Level level;
    public int lifetime = 0;
    public boolean supportCalled = false;
    public MarkerItem.TYPE type;
    public Entity owner;


    public MarkerMarker(Vec3 pos, Level level, MarkerItem.TYPE type, Entity owner) {
        this.pos = pos;
        this.level = level;
        this.type = type;
        this.owner = owner;
        this.vel = owner.getDeltaMovement();
    }
}
