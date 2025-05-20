package com.coderdan.avaritia.screen;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.screen.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Avaritia.MOD_ID);

    public static final RegistryObject<MenuType<CrafterMenu>> CRAFTER_MENU = MENUS.register("crafter_name", () -> IForgeMenuType.create(CrafterMenu::new));
    public static final RegistryObject<MenuType<CompressorMenu>> COMPRESSOR = MENUS.register("compressor_name", () -> IForgeMenuType.create(CompressorMenu::new));
    public static final RegistryObject<MenuType<CollectorMenu>> COLLECTOR = MENUS.register("collector_name", () -> IForgeMenuType.create(CollectorMenu::new));
    public static final RegistryObject<MenuType<ExtremeCraftingMenu>> EXTREME_CRAFTING = MENUS.register("extreme_name", () -> IForgeMenuType.create(ExtremeCraftingMenu::new));
    public static final RegistryObject<MenuType<CompressedCraftingMenu>> COMPRESSED_CRAFTING = MENUS.register(
            "compressed_crafting_name",
            () -> IForgeMenuType.create((id, inv, buf) -> new CompressedCraftingMenu(id, inv))
    );


    public static void register(IEventBus bus)
    {
        MENUS.register(bus);
    }
}
