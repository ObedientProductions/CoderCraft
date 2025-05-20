package com.coderdan.avaritia;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.entity.ModBlockEntities;
import com.coderdan.avaritia.entity.renderer.CrafterEntityRenderer;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.armor.ArmorModelLayers;
import com.coderdan.avaritia.item.creativetabs.ModCreativeTabs;
import com.coderdan.avaritia.recipe.ModRecipies;
import com.coderdan.avaritia.screen.ModMenuTypes;
import com.coderdan.avaritia.screen.custom.*;
import com.coderdan.avaritia.sound.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Avaritia.MOD_ID)
public class Avaritia
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "avaritia";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Avaritia(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModCreativeTabs.register(modEventBus);

        ModMenuTypes.register(modEventBus); // ← you’re missing this line
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        ModRecipies.register(modEventBus);





        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.CRAFTER_MENU.get(), CrafterScreen::new);
            MenuScreens.register(ModMenuTypes.COMPRESSOR.get(), CompressorScreen::new);
            MenuScreens.register(ModMenuTypes.COLLECTOR.get(), CollectorScreen::new);
            MenuScreens.register(ModMenuTypes.EXTREME_CRAFTING.get(), ExtremeCraftingScreen::new);





        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event)
        {
            event.registerBlockEntityRenderer(ModBlockEntities.CRAFTER.get(), CrafterEntityRenderer::new);
        }


        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            ArmorModelLayers.register(event);
        }



    }


    @Mod.EventBusSubscriber(modid = Avaritia.MOD_ID) // Not limited to client
    public class CommonModEvents {

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    if (player.containerMenu instanceof ExtremeCraftingMenu menu) {
                        menu.serverTick();
                    }
                }
            }
        }
    }

}
