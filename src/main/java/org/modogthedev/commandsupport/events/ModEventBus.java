package org.modogthedev.commandsupport.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.core.ModParticles;
import org.modogthedev.commandsupport.custom.particles.MarkerParticle;
import org.modogthedev.commandsupport.custom.particles.PlaneParticle;


@Mod.EventBusSubscriber(modid = Commandsupport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus {

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.PLANE_PARTICLE.get(),
                PlaneParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.MARKER_PARTICLE.get(),
                MarkerParticle.Provider::new);
    }
}
