package com.coderdan.avaritia.item;


import com.coderdan.avaritia.Avaritia;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Avaritia.MOD_ID);

    public static final RegistryObject<DataComponentType<BlockPos>> IS_WORLD_BREAKING = register("used",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final RegistryObject<DataComponentType<BlockPos>> IS_FULL = register("full",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final RegistryObject<DataComponentType<List<ItemStack>>> STORED_LOOT =
            register("stored_loot", builder -> builder.persistent(ItemStack.CODEC.listOf()));

    public static final RegistryObject<DataComponentType<Float>> IS_INFINITY_TRIMMED =
            register("infinity_trimmed", builder -> builder.persistent(Codec.FLOAT));





    private static <T>RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
