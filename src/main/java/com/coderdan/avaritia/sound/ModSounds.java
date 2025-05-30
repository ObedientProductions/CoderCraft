package com.coderdan.avaritia.sound;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Avaritia.MOD_ID);

    public static final RegistryObject<SoundEvent> TRANSMUTER_BLOCK_USE = registerSoundEvent("transmuter_block_use");
    public static final RegistryObject<SoundEvent> GAPING_VOID = registerSoundEvent("gaping_void");
    public static final RegistryObject<SoundEvent> QUACK = registerSoundEvent("quack");

    //public static final ForgeSoundType TRANSMUTER_BLOCK_SOUNDS = new ForgeSoundType(1f, 1f, ModSounds.TRANSMUTER_BLOCK_USE)

    public static final RegistryObject<SoundEvent> DNB_BEATBOX = registerSoundEvent("dnb_musicdisc");
    public static final ResourceKey<JukeboxSong> DNB_BEATBOX_KEY = ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "dnb_musicdisc"));

    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, name)));
    }

    public static void register(IEventBus bus)
    {
        SOUND_EVENTS.register(bus);
    }


}
