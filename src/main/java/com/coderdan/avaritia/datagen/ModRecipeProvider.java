package com.coderdan.avaritia.datagen;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {

       // ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.INFINITY_BLOCK.get())
          //      .pattern("AAA")
          //      .pattern("AAA")
          //      .pattern("AAA")
           //     .define('A', ModItems.INFINITY_BOW.get())
          //      .unlockedBy(getHasName(ModItems.INFINITY_BOW.get()), has(ModItems.INFINITY_BOW.get())).save(pRecipeOutput);


       // ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COSMIC_MEATBALLS.get(), 9)
            //    .requires(ModBlocks.INFINITY_BLOCK.get())
            //    .unlockedBy(getHasName(ModBlocks.INFINITY_BLOCK.get()), has(ModBlocks.INFINITY_BLOCK.get())).save(pRecipeOutput);


        trimSmithing(pRecipeOutput, ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get(), ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "heavens_mark"));
    }



    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Avaritia.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
