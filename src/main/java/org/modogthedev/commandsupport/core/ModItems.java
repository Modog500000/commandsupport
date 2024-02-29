package org.modogthedev.commandsupport.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.custom.items.MarkerItem;
import org.modogthedev.commandsupport.custom.items.RadioItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Commandsupport.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            Commandsupport.MODID);

    public static RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RADIO.get()))
                    .title(Component.translatable("itemGroup.commandsupport")).build());

    public static void registerCreativeTabs(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
    public static final RegistryObject<Item> RADIO = ITEMS.register("radio", () -> new RadioItem(new  Item.Properties()));
    public static final RegistryObject<Item> MARKER_SUPPLY = ITEMS.register("support_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.SUPPLY),new Item.Properties()));
    public static final RegistryObject<Item> MARKER_AIRSTRIKE = ITEMS.register("airstrike_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.AIRSTRIKE),new Item.Properties()));
    public static final RegistryObject<Item> MARKER_NUKE = ITEMS.register("nuke_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.NUKE),new Item.Properties()));
}
