package com.coderdan.avaritia.command;

import com.coderdan.avaritia.block.entity.custom.ExtremeCraftingTableBlockEntity;
import com.coderdan.avaritia.mixin.RecipeManagerAccessor;
import com.coderdan.avaritia.recipe.ExtremeCraftingPattern;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipe;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipeInput;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.items.ItemStackHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RemoveExtremeRecipeCommand {

    public static int run(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos posBelow = player.blockPosition().below();
            Level level = player.level();

            BlockEntity be = level.getBlockEntity(posBelow);
            if (!(be instanceof ExtremeCraftingTableBlockEntity table)) {
                source.sendFailure(net.minecraft.network.chat.Component.literal("You must be standing on an Extreme Crafting Table."));
                return 0;
            }

            ItemStack[] inputs = new ItemStack[81];
            for (int i = 0; i < 81; i++) {
                inputs[i] = table.inventory.getStackInSlot(i);
            }
            ExtremeCraftingPattern currentPattern = ExtremeCraftingPattern.fromItems(inputs);


            RecipeManager manager = player.getServer().getRecipeManager();
            RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

            Map<ResourceLocation, RecipeHolder<?>> current = new HashMap<>(accessor.getByName());
            ArrayListMultimap<RecipeType<?>, RecipeHolder<?>> currentByType = ArrayListMultimap.create(accessor.getByType());

            ResourceLocation match = null;
            RecipeHolder<?> matchedHolder = null;

            for (Map.Entry<ResourceLocation, RecipeHolder<?>> entry : current.entrySet()) {
                if (entry.getValue().value() instanceof ExtremeCraftingRecipe recipe) {
                    ExtremeCraftingRecipeInput inputWrapper = new ExtremeCraftingRecipeInput(sliceHandler(table.inventory, 81));

                    if (recipe.matches(inputWrapper, level)) {
                        match = entry.getKey();
                        matchedHolder = entry.getValue();
                        break;
                    }
                }
            }



            if (match != null) {
                current.remove(match);
                currentByType.remove(matchedHolder.value().getType(), matchedHolder);
                manager.replaceRecipes(current.values());


                File folder = new File(player.getServer().getWorldPath(LevelResource.ROOT).toFile(), "data/avaritia/custom_recipes");
                // Always write a .remove.json marker
                File removeFile = new File(folder, match.getPath() + ".remove.json");
                try (var writer = new java.io.FileWriter(removeFile)) {
                    writer.write("{\"id\": \"" + match.toString() + "\"}");
                } catch (Exception ex) {
                    source.sendFailure(Component.literal("Failed to write removal file: " + removeFile.getName()));
                }

                // Try to delete the .json file if it's a custom recipe
                File recipeFile = new File(folder, match.getPath() + ".json");
                if (recipeFile.exists()) {
                    boolean deleted = recipeFile.delete();
                    if (!deleted) {
                        source.sendFailure(Component.literal("Failed to delete file: " + recipeFile.getName()));
                    }
                }



                ResourceLocation finalMatch = match;
                source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Removed recipe: " + finalMatch), true);
                return 1;
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("No matching recipe found in grid."));
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            source.sendFailure(net.minecraft.network.chat.Component.literal("Command failed: " + e.getMessage()));
            return 0;
        }
    }


    private static ItemStackHandler sliceHandler(ItemStackHandler original, int size) {
        ItemStackHandler sliced = new ItemStackHandler(size);
        for (int i = 0; i < size; i++) {
            sliced.setStackInSlot(i, original.getStackInSlot(i));
        }
        return sliced;
    }

}
