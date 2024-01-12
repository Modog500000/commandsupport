package org.modogthedev.commandsupport.core;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.commandsupport.Commandsupport;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Commandsupport.MODID);

    public static final RegistryObject<SimpleParticleType> PLANE_PARTICLE =
            PARTICLE_TYPES.register("plane_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> MARKER_PARTICLE =
            PARTICLE_TYPES.register("marker_particle", () -> new SimpleParticleType(true));
}
