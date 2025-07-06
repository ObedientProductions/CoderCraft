package com.coderdan.avaritia.command;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.ExtremeCraftingTableBlockEntity;
import com.coderdan.avaritia.mixin.RecipeManagerAccessor;
import com.coderdan.avaritia.recipe.ExtremeCraftingPattern;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipe;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipeInput;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartedEvent;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;


public class AddExtremeRecipeCommand {
    public static int run(CommandSourceStack source, ItemStack output, int count){

        try {


            ServerPlayer player = source.getPlayerOrException();
            BlockPos posBelow = player.blockPosition().below();
            Level level = player.level();

            BlockEntity be = level.getBlockEntity(posBelow);
            if (!(be instanceof ExtremeCraftingTableBlockEntity table)) {
                source.sendFailure(net.minecraft.network.chat.Component.literal("You must be standing on an Extreme Crafting Table."));
                return 0;
            }

            // Extract input items from inventory
            ItemStack[] inputs = new ItemStack[81];
            for (int i = 0; i < 81; i++) {
                inputs[i] = table.inventory.getStackInSlot(i);
            }

            boolean empty = Arrays.stream(inputs).allMatch(ItemStack::isEmpty);
            if (empty) {
                source.sendFailure(Component.literal("Can't create recipe: the grid is empty."));
                return 0;
            }

            if (table.inventory == null) {
                source.sendFailure(Component.literal("Crafting table inventory is not initialized."));
                return 0;
            }


            ExtremeCraftingPattern pattern = ExtremeCraftingPattern.fromItems(inputs);
            output.setCount(count);
            ExtremeCraftingRecipe recipe = new ExtremeCraftingRecipe(pattern, output.copy());


            // Inject into recipe manager (requires accessor)
            RecipeManager manager = player.getServer().getRecipeManager();
            RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

            // Copy old recipes
            Map<ResourceLocation, RecipeHolder<?>> newByName = new java.util.HashMap<>(accessor.getByName());
            ArrayListMultimap<RecipeType<?>, RecipeHolder<?>> newByType = ArrayListMultimap.create(accessor.getByType());

            // Add the new recipe
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "custom_" + System.currentTimeMillis());
            RecipeHolder<ExtremeCraftingRecipe> holder = new RecipeHolder<>(id, recipe);
            newByName.put(id, holder);
            newByType.put(recipe.getType(), holder);

            // Replace them
            manager.replaceRecipes(newByName.values());


            File folder = new File(player.getServer().getWorldPath(LevelResource.ROOT).toFile(), "data/avaritia/custom_recipes");
            folder.mkdirs();

            File outFile = new File(folder, id.getPath() + ".json");

            JsonObject json = new JsonObject();
            json.addProperty("type", "avaritia:extreme"); // your recipe type
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            json.add("pattern", gson.toJsonTree(pattern.toPatternList()));
            JsonObject keyJson = new JsonObject();
            for (Map.Entry<String, Ingredient> entry : pattern.toKeyMap().entrySet()) {
                JsonElement encodedIngredient = Ingredient.CODEC_NONEMPTY
                        .encodeStart(JsonOps.INSTANCE, entry.getValue())
                        .getOrThrow(); // no need for custom error here, let it throw if it breaks

                keyJson.add(String.valueOf(entry.getKey()), encodedIngredient);
            }
            json.add("key", keyJson);


            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(output.getItem());

            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("id", itemId.toString());

            if (output.getCount() > 1) {
                resultJson.addProperty("count", output.getCount());
            }
            json.add("result", resultJson);



            try (FileWriter writer = new FileWriter(outFile)) {
                gson.toJson(json, writer); // use the same gson
            }






            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Added recipe for " + output.getDisplayName().getString()), true);
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace(); // shows in console
            source.sendFailure(Component.literal("Command failed: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
            return 0;
        }
    }
}
