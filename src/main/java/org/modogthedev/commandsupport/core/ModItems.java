package org.modogthedev.commandsupport.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.custom.items.MarkerItem;
import org.modogthedev.commandsupport.custom.items.RadioItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Commandsupport.MODID);

    public static final RegistryObject<Item> RADIO = ITEMS.register("radio", () -> new RadioItem(new  Item.Properties()));
    public static final RegistryObject<Item> MARKER_SUPPLY = ITEMS.register("support_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.SUPPLY),new Item.Properties()));
    public static final RegistryObject<Item> MARKER_AIRSTRIKE = ITEMS.register("airstrike_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.AIRSTRIKE),new Item.Properties()));
    public static final RegistryObject<Item> MARKER_NUKE = ITEMS.register("nuke_flare", () -> new MarkerItem(new MarkerItem.MarkerProperties().type(MarkerItem.TYPE.NUKE),new Item.Properties()));
}
