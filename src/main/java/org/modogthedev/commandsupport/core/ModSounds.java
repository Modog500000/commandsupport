package org.modogthedev.commandsupport.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.commandsupport.Commandsupport;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Commandsupport.MODID);

    public static final RegistryObject<SoundEvent> ALARM = registerSoundEvent("alarm");
    public static final RegistryObject<SoundEvent> RADIO = registerSoundEvent("radio");
    public static final RegistryObject<SoundEvent> FLARE = registerSoundEvent("flare");
    public static final RegistryObject<SoundEvent> FLYBY = registerSoundEvent("flyby");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Commandsupport.MODID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Commandsupport.MODID, name)));
    };
}
