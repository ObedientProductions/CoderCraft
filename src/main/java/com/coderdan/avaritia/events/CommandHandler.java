package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.command.AddExtremeRecipeCommand;
import com.coderdan.avaritia.command.AddExtremeShapelessRecipeCommand;
import com.coderdan.avaritia.command.RemoveExtremeRecipeCommand;
import com.coderdan.avaritia.mixin.RecipeManagerAccessor;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipe;
import com.coderdan.avaritia.recipe.ModRecipies;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.impl.Pair;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID)
public class CommandHandler {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var context = event.getBuildContext(); // this is the missing arg

        dispatcher.register(
                Commands.literal("addExtremeCraftingRecipe")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("output", ItemArgument.item(context))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes(ctx -> AddExtremeRecipeCommand.run(
                                                ctx.getSource(),
                                                ItemArgument.getItem(ctx, "output").createItemStack(IntegerArgumentType.getInteger(ctx, "count"), false),
                                                IntegerArgumentType.getInteger(ctx, "count")
                                        ))
                                )
                        )

        );

           /* dispatcher.register(
                    Commands.literal("addExtremeCraftingRecipeShapeless")
                            .requires(source -> source.hasPermission(2))
                            .then(Commands.argument("output", ItemArgument.item(context))
                                    .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                            .executes(ctx -> AddExtremeShapelessRecipeCommand.run(
                                                    ctx.getSource(),
                                                    ItemArgument.getItem(ctx, "output").createItemStack(IntegerArgumentType.getInteger(ctx, "count"), false),
                                                    IntegerArgumentType.getInteger(ctx, "count")
                                            ))
                                    )
                            )

            );*/

        dispatcher.register(
                Commands.literal("removeExtremeCraftingRecipe")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> RemoveExtremeRecipeCommand.run(ctx.getSource()))
        );
    }


    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) throws FileNotFoundException {
        MinecraftServer server = event.getServer();
        File folder = new File(server.getWorldPath(LevelResource.ROOT).toFile(), "data/avaritia/custom_recipes");
        if (!folder.exists()) return;

        RecipeManager manager = server.getRecipeManager();
        RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

        // Copy current recipes
        Map<ResourceLocation, RecipeHolder<?>> newByName = new HashMap<>(accessor.getByName());
        ArrayListMultimap<RecipeType<?>, RecipeHolder<?>> newByType = ArrayListMultimap.create(accessor.getByType());

        // Step 1: collect recipe IDs to remove
        Set<ResourceLocation> removeList = new HashSet<>();
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".remove.json")) {
                JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
                String idStr = GsonHelper.getAsString(json, "id");
                ResourceLocation id = ResourceLocation.parse(idStr);
                removeList.add(id);
            }
        }

        // Step 2: remove them from recipe maps
        for (ResourceLocation id : removeList) {
            RecipeHolder<?> removed = newByName.remove(id);
            if (removed != null) {
                newByType.remove(removed.value().getType(), removed);
                Avaritia.LOGGER.info("Removed recipe {}", id);
            } else {
                Avaritia.LOGGER.warn("No recipe found to remove: {}", id);
            }
        }

        // Step 3: Add custom recipes
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".json") || file.getName().endsWith(".remove.json")) continue;

            JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

            DataResult<MapLike<JsonElement>> mapResult = JsonOps.INSTANCE.getMap(json);
            MapLike<JsonElement> map = mapResult
                    .result()
                    .orElseThrow(() -> new IllegalStateException("Invalid JSON format in " + file.getName()));

            DataResult<ExtremeCraftingRecipe> result = ModRecipies.EXTREMECRAFTING_SERIALIZER.get()
                    .codec()
                    .decode(JsonOps.INSTANCE, map);

            if (result.error().isPresent()) {
                Avaritia.LOGGER.error("Decode error in {}: {}", file.getName(), result.error().get().message());
            }

            ExtremeCraftingRecipe recipe = result
                    .result()
                    .orElseThrow(() -> new IllegalStateException("Failed to load recipe from " + file.getName()));

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    Avaritia.MOD_ID,
                    file.getName().replace(".json", "")
            );

            // skip if this was marked for removal
            if (removeList.contains(id)) continue;

            RecipeHolder<ExtremeCraftingRecipe> holder = new RecipeHolder<>(id, recipe);
            newByName.put(id, holder);
            newByType.put(recipe.getType(), holder);
        }

        manager.replaceRecipes(newByName.values());
    }

}

