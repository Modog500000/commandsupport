package org.modogthedev.commandsupport.markers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.commandsupport.custom.items.MarkerItem;

public class SupportMarker {
    public Vec3 pos;
    public Level level;
    public int lifespan = 0;
    public MarkerMarker marker;
    public MarkerItem.TYPE type;
    public Entity owner;
    public boolean hasDeployed = false;
    public boolean hasParticle = false;
    public int delay = 0;

    public SupportMarker(Vec3 pos, Level level, MarkerMarker marker, MarkerItem.TYPE type, Entity owner) {
        this.pos = pos;
        this.level = level;
        this.marker = marker;
        this.type = type;
        this.owner = owner;
    }
}
