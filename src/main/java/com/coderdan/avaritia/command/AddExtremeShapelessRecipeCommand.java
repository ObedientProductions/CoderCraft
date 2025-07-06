package com.coderdan.avaritia.command;


import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.ExtremeCraftingTableBlockEntity;
import com.coderdan.avaritia.mixin.RecipeManagerAccessor;
import com.coderdan.avaritia.recipe.ExtremeShapelessRecipe;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddExtremeShapelessRecipeCommand {
    public static int run(CommandSourceStack source, ItemStack output, int count) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            BlockPos posBelow = player.blockPosition().below();
            Level level = player.level();

            BlockEntity be = level.getBlockEntity(posBelow);
            if (!(be instanceof ExtremeCraftingTableBlockEntity table)) {
                source.sendFailure(net.minecraft.network.chat.Component.literal("You must be standing on an Extreme Crafting Table."));
                return 0;
            }

            List<Ingredient> ingredients = new ArrayList<>();
            for (int i = 0; i < 81; i++) {
                ItemStack stack = table.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ingredients.add(Ingredient.of(stack));
                }
            }

            if (ingredients.isEmpty()) {
                source.sendFailure(net.minecraft.network.chat.Component.literal("Can't create recipe: the grid is empty."));
                return 0;
            }

            output.setCount(count);
            ExtremeShapelessRecipe recipe = new ExtremeShapelessRecipe(ingredients, output.copy());

            // Inject into RecipeManager
            RecipeManager manager = player.getServer().getRecipeManager();
            RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

            Map<ResourceLocation, RecipeHolder<?>> newByName = new HashMap<>(accessor.getByName());
            ArrayListMultimap<RecipeType<?>, RecipeHolder<?>> newByType = ArrayListMultimap.create(accessor.getByType());

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "shapeless_" + System.currentTimeMillis());
            RecipeHolder<ExtremeShapelessRecipe> holder = new RecipeHolder<>(id, recipe);

            newByName.put(id, holder);
            newByType.put(recipe.getType(), holder);

            manager.replaceRecipes(newByName.values());

            // Save to file
            File folder = new File(player.getServer().getWorldPath(LevelResource.ROOT).toFile(), "data/avaritia/custom_recipes");
            folder.mkdirs();

            File outFile = new File(folder, id.getPath() + ".json");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            JsonObject json = new JsonObject();
            json.addProperty("type", "avaritia:extreme_shapeless");

            JsonArray ingredientsJson = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ingredientsJson.add(Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow());
            }
            json.add("ingredients", ingredientsJson);

            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("id", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.getItem()).toString());
            if (output.getCount() > 1) {
                resultJson.addProperty("count", output.getCount());
            }
            json.add("result", resultJson);

            try (FileWriter writer = new FileWriter(outFile)) {
                gson.toJson(json, writer);
            }

            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Added shapeless recipe for " + output.getDisplayName().getString()), true);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            source.sendFailure(net.minecraft.network.chat.Component.literal("Command failed: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
            return 0;
        }
    }
}