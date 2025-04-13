package com.coderdan.codercraft.entity;

import com.coderdan.codercraft.CoderCraft;
import com.coderdan.codercraft.block.ModBlocks;
import com.coderdan.codercraft.block.entity.custom.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoderCraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrafterBlockEntity>> CRAFTER = BLOCK_ENTITIES.register("crafter", () -> BlockEntityType.Builder.of(
            CrafterBlockEntity::new, ModBlocks.CRAFTER.get()).build(null));





    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
