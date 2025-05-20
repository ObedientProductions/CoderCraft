package com.coderdan.avaritia.entity;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.block.entity.custom.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Avaritia.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrafterBlockEntity>> CRAFTER = BLOCK_ENTITIES.register("crafter", () -> BlockEntityType.Builder.of(
            CrafterBlockEntity::new, ModBlocks.CRAFTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompressorBlockEntity>> COMPRESSOR = BLOCK_ENTITIES.register("compressor", () -> BlockEntityType.Builder.of(
            CompressorBlockEntity::new, ModBlocks.COMPRESSOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CollectorBlockEntity>> COLLECTOR = BLOCK_ENTITIES.register("collector", () -> BlockEntityType.Builder.of(
            CollectorBlockEntity::new, ModBlocks.COLLECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ExtremeCraftingTableBlockEntity>> EXTREME_CRAFTING_TABLE = BLOCK_ENTITIES.register("extreme_crafting_table", () -> BlockEntityType.Builder.of(
            ExtremeCraftingTableBlockEntity::new, ModBlocks.EXTREME_CRAFTING_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompressedCraftingTableBlockEntity>> COMPRESSED_CRAFTING_TABLE = BLOCK_ENTITIES.register("compressed_crafting_table", () -> BlockEntityType.Builder.of(
            CompressedCraftingTableBlockEntity::new, ModBlocks.COMPRESSED_CRAFTING_TABLE.get()).build(null));





    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
