package com.coderdan.codercraft.item.creativetabs;

import com.coderdan.codercraft.CoderCraft;
import com.coderdan.codercraft.block.ModBlocks;
import com.coderdan.codercraft.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoderCraft.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CODERCRAFT_TAB = CREATIVE_MODE_TABS.register("codercraft_tab",
            () -> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(ModItems.TITANIUM.get()))
                    .title(Component.translatable("creativetab.codercraft.codercraft_tab"))
                    .displayItems((itemDisplayParameters, output) ->
                        {
                            output.accept(ModItems.TITANIUM.get());
                            output.accept(ModItems.TITANIUM_INGOT.get());
                            output.accept(ModBlocks.TITANIUM_BLOCK.get());
                            output.accept(ModBlocks.RAW_TITANIUM_BLOCK.get());
                            output.accept(ModItems.SPECIAL_ITEM.get());
                            output.accept(ModBlocks.TRANSMUTER_BLOCK.get());
                            output.accept(ModBlocks.CRAFTER.get());
                            output.accept(ModBlocks.CRAFTER.get());
                        })

                    .build());

    public static void register(IEventBus bus)
    {
        CREATIVE_MODE_TABS.register(bus);
    }
}
