package org.modogthedev.commandsupport;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.commandsupport.core.ModItems;
import org.modogthedev.commandsupport.core.ModParticles;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.markers.MarkerHandeler;
import org.modogthedev.commandsupport.networking.Messages;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Commandsupport.MODID)
public class Commandsupport {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "commandsupport";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public Commandsupport() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.registerCreativeTabs(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        Messages.register();

        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(MarkerHandeler::tick);
        bus.addListener(MarkerHandeler::tickLevel);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModItems.TAB.get()) {
            event.accept(ModItems.RADIO);
            event.accept(ModItems.MARKER_AIRSTRIKE);
            event.accept(ModItems.MARKER_NUKE);
            event.accept(ModItems.MARKER_SUPPLY);
        }
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            IEventBus bus = MinecraftForge.EVENT_BUS;
            bus.addListener(MarkerHandeler::clientTick);
        }
    }
}
