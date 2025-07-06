package com.coderdan.avaritia.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor("byName")
    Map<ResourceLocation, RecipeHolder<?>> getByName();

    @Accessor("byType")
    Multimap<RecipeType<?>, RecipeHolder<?>> getByType();
}

