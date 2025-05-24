package com.coderdan.avaritia.datagen;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture,
                              CompletableFuture<TagLookup<Block>> lookupCompletableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, lookupCompletableFuture, Avaritia.MOD_ID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.INFINITY_HELMET.get())
                .add(ModItems.INFINITY_BREASTPLATE.get())
                .add(ModItems.INFINITY_LEGGINGS.get())
                .add(ModItems.INFINITY_BOOTS.get());

        tag(ItemTags.TRIM_MATERIALS)
                .add(ModItems.INFINITY_INGOT.get());

        tag(ItemTags.TRIM_TEMPLATES)
                .add(ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get());
    }
}
