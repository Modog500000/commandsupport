package org.modogthedev.commandsupport.networking.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.markers.MarkerHandeler;
import org.modogthedev.commandsupport.markers.MarkerMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketSyncMarkers {
    private final CompoundTag markers;


    public PacketSyncMarkers(FriendlyByteBuf buf) {
        markers = buf.readNbt();
    }

    public PacketSyncMarkers(CompoundTag markers) {
        this.markers = markers;

    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(markers);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // Here we are client side.
            // Be very careful not to access client-only classes here! (like Minecraft) because
            // this packet needs to be available server-side too
            MarkerHandeler.toHandel.add(new MarkerMarker(markers));
        });
        return true;
    }
}
