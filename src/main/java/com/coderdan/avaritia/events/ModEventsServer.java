package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventsServer {

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        ModConfig.bakeConfig(); // ← Calls the method to cache the config value
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        ModConfig.bakeConfig(); // ← Also rebake on config reload (optional but good)
    }
}

