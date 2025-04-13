package com.coderdan.codercraft.item;

import com.coderdan.codercraft.CoderCraft;
import com.coderdan.codercraft.item.custom.Specialitem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoderCraft.MOD_ID);

    public static final RegistryObject<Item> TITANIUM = ITEMS.register("titanium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("special_item",
            () -> new Specialitem(new Item.Properties().durability(32)));

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
