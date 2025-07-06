package com.coderdan.compat;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.recipe.*;
import com.coderdan.avaritia.screen.ModMenuTypes;
import com.coderdan.avaritia.screen.custom.CompressorMenu;
import com.coderdan.avaritia.screen.custom.CompressorScreen;
import com.coderdan.avaritia.screen.custom.ExtremeCraftingMenu;
import com.coderdan.avaritia.screen.custom.ExtremeCraftingScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIAvaritiaPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ExtremeRecipeCatagory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CompressorRecipeCatagory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CollectorRecipeCatagory(registration.getJeiHelpers().getGuiHelper()));
    }



    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Get shaped recipes
        List<ExtremeCraftingRecipe> extremeCraftingRecipes = recipeManager
                .getAllRecipesFor(ModRecipies.EXTREMECRAFTING_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        // Get shapeless recipes
        List<ExtremeShapelessRecipe> extremeShapelessCraftingRecipes = recipeManager
                .getAllRecipesFor(ModRecipies.EXTREMECRAFTING_SHAPELESS_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        // Combine them
        List<Recipe<ExtremeCraftingRecipeInput>> allExtremeRecipes = new java.util.ArrayList<>();
        allExtremeRecipes.addAll(extremeCraftingRecipes);
        allExtremeRecipes.addAll(extremeShapelessCraftingRecipes);


        // Register under one category
        registration.addRecipes(ExtremeRecipeCatagory.EXTREMECRAFTING_RECIPE_TYPE, allExtremeRecipes);

        List<CompressorRecipe> compressorRecipes = recipeManager
                .getAllRecipesFor(ModRecipies.COMPRESSOR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> {
                    Ingredient ingredient = recipe.getIngredients().get(0);
                    ItemStack[] stacks = ingredient.getItems();

                    System.out.println(ingredient + " TEST098");

                    // Check for barrier fallback
                    if (stacks[0].is(Items.BARRIER)) {
                        System.out.println("Skipping recipe for " + recipe.output().getDisplayName().getString() + " (ingredient fallback is barrier)");
                        return false;
                    }

                    return true;
                })
                .toList();


        // Register only the valid ones
        registration.addRecipes(CompressorRecipeCatagory.COMPRESSION_RECIPE_TYPE, compressorRecipes);


        //Collector Recipe Register
        List<CollectorRecipeDisplay> displays = List.of(
                new CollectorRecipeDisplay(new ItemStack(Items.AIR), new ItemStack(ModItems.PILE_OF_NEUTRONS.get()))
        );

        registration.addRecipes(CollectorRecipeCatagory.COLLECTION_RECIPE_TYPE, displays);

        registration.addItemStackInfo(
                new ItemStack(ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get()),
                Component.literal("A long-lost armor trim said to be worn only by the gods. So rare, it’s believed only a handful ever existed."),
                Component.literal("Legends whisper of a lone pirate who once claimed one in the End — and vanished without a trace.")
        );


    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
        IModPlugin.super.registerModInfo(modAliasRegistration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ExtremeCraftingScreen.class, 176 , 80 , 22, 13, ExtremeRecipeCatagory.EXTREMECRAFTING_RECIPE_TYPE);
        registration.addRecipeClickArea(CompressorScreen.class, 61 , 35 , 22, 16, CompressorRecipeCatagory.COMPRESSION_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.EXTREME_CRAFTING_TABLE.get().asItem()),
                ExtremeRecipeCatagory.EXTREMECRAFTING_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COMPRESSOR.get().asItem()),
                CompressorRecipeCatagory.COMPRESSION_RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COLLECTOR.get().asItem()),
                CollectorRecipeCatagory.COLLECTION_RECIPE_TYPE);
    }


    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IModPlugin.super.registerRecipeTransferHandlers(registration);

        registration.addRecipeTransferHandler(
                ExtremeCraftingMenu.class,
                ModMenuTypes.EXTREME_CRAFTING.get(),
                ExtremeRecipeCatagory.EXTREMECRAFTING_RECIPE_TYPE,
                36, 81,   // crafting input range
                0, 36     // player inventory range
        );

        registration.addRecipeTransferHandler(
                CompressorMenu.class,
                ModMenuTypes.COMPRESSOR.get(),
                CompressorRecipeCatagory.COMPRESSION_RECIPE_TYPE,
                36 + 1, 1,   // index 37, count 1 (real input slot is after 36 player slots and 1 ghost slot)
                0, 36        // player inventory range (0–35)
        );



    }




}
