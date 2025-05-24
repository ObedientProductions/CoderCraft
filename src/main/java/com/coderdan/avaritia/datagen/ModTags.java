package com.coderdan.avaritia.datagen;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_INFINITY_TOOL =
                TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "needs_infinity_tool"));

        public static final TagKey<Block> INCORRECT_FOR_INFINITY_TOOL =
                TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "incorrect_for_infinity_tool.json"));
    }
}

