package com.coderdan.codercraft.sound;

import com.coderdan.codercraft.CoderCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CoderCraft.MOD_ID);

    public static final RegistryObject<SoundEvent> TRANSMUTER_BLOCK_USE = registerSoundEvent("transmuter_block_use");

    //public static final ForgeSoundType TRANSMUTER_BLOCK_SOUNDS = new ForgeSoundType(1f, 1f, ModSounds.TRANSMUTER_BLOCK_USE)

    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CoderCraft.MOD_ID, name)));
    }

    public static void register(IEventBus bus)
    {
        SOUND_EVENTS.register(bus);
    }


}
