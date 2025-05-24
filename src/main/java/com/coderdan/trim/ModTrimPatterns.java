package com.coderdan.trim;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTrimPatterns {
    public static final ResourceKey<TrimPattern> HEAVENS_MARK = ResourceKey.create(Registries.TRIM_PATTERN,
            ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "heavens_mark"));

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        register(context, ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get(), HEAVENS_MARK);
    }

    private static void register(BootstrapContext<TrimPattern> context, Item item, ResourceKey<TrimPattern> key) {
        TrimPattern trimPattern = new TrimPattern(key.location(), ForgeRegistries.ITEMS.getHolder(item).get(),
                Component.translatable(Util.makeDescriptionId("trim_pattern", key.location())), false);
        context.register(key, trimPattern);
    }
}