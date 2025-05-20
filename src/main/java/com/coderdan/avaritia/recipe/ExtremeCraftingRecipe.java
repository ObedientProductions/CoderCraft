package com.coderdan.avaritia.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents one "Extreme Crafting" recipe:
 * A shaped crafting recipe (similar to vanilla shaped crafting)
 * but supporting a 9x9 grid instead of 3x3.
 *
 * Each recipe contains:
 * - A crafting pattern (grid of ingredients)
 * - The resulting item when crafted
 */
public record ExtremeCraftingRecipe(
        ExtremeCraftingPattern pattern, // stores the crafting grid layout (9x9)
        ItemStack output                 // stores the crafted result item
) implements Recipe<ExtremeCraftingRecipeInput> { // Recipe input is our custom 81-slot input

    /**
     * Returns the list of ingredients for this recipe.
     * For now, we use the default behavior inherited from Recipe.
     */
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> padded = NonNullList.create();
        padded.addAll(pattern.ingredients());

        while (padded.size() < 81) {
            padded.add(Ingredient.EMPTY);
        }

        return padded;
    }



    /**
     * Checks if a crafting grid matches this recipe.
     *
     * Critical part:
     * - Loops through every slot in the 9x9 pattern
     * - Compares the expected Ingredient with the actual ItemStack in the crafting input
     * - If any slot doesn't match, return false
     */
    @Override
    public boolean matches(ExtremeCraftingRecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide()) return false; // Don't match recipes client-side

        for (int y = 0; y < pattern.height(); y++) {
            for (int x = 0; x < pattern.width(); x++) {
                int index = y * pattern.width() + x; // linear index
                Ingredient expected = pattern.ingredients().get(index);
                ItemStack actual = pInput.getItem(index);

                if (!expected.test(actual)) {
                    System.out.println("Mismatch at slot " + index + ": expected " + expected + ", found " + actual);
                    return false; // Exit early if anything doesn't match
                }
            }
        }

        System.out.println("ExtremeCraftingRecipe MATCH SUCCESS!");
        return true; // Everything matched
    }

    /**
     * Creates the output item when crafting succeeds.
     *
     * Always returns a fresh copy of the output.
     */
    @Override
    public ItemStack assemble(ExtremeCraftingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    /**
     * Determines if the recipe fits inside the given grid size.
     *
     * Right now always returns true,
     * because our block will always be big enough (9x9).
     */
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    /**
     * Returns what item is produced (used by Minecraft to show recipe outputs).
     */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    /**
     * Returns the serializer used to save/load this recipe type.
     */
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipies.EXTREMECRAFTING_SERIALIZER.get();
    }

    /**
     * Returns the type of recipe this is.
     */
    @Override
    public RecipeType<?> getType() {
        return ModRecipies.EXTREMECRAFTING_TYPE.get();
    }

    /**
     * Serializer nested class.
     * Responsible for loading and saving ExtremeCraftingRecipe from JSON and network.
     */
    public static class Serializer implements RecipeSerializer<ExtremeCraftingRecipe> {

        /**
         * Codec for reading recipes from JSON.
         *
         * We STRUGGLED here because:
         * - We can't directly save "pattern" or "key" because they aren't stored after parsing
         * - So we throw exceptions if Minecraft tries to serialize them
         * - But we can still read them successfully
         */
        public static final MapCodec<ExtremeCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(Codec.STRING).fieldOf("pattern").forGetter(r -> {
                    throw new UnsupportedOperationException("Saving pattern not supported yet.");
                }),
                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(r -> {
                    throw new UnsupportedOperationException("Saving key not supported yet.");
                }),
                ItemStack.CODEC.fieldOf("result").forGetter(ExtremeCraftingRecipe::output)
        ).apply(instance, (patternLines, key, resultItem) -> {
            ExtremeCraftingPattern pattern = ExtremeCraftingPattern.fromKey(patternLines, key);
            return new ExtremeCraftingRecipe(pattern, resultItem);
        }));

        /**
         * Codec for sending recipes across network packets (server -> client sync).
         *
         * We encode:
         * - The pattern (ingredients, width, height)
         * - The output ItemStack
         *
         *
         * We STRUGGLED here because:
         * - If we didn't encode the pattern size correctly (like sending 90 slots instead of 81),
         *   Minecraft would crash during packet sync.
         */
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtremeCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, // how to write recipe to network
                Serializer::fromNetwork // how to read recipe from network
        );

        @Override
        public MapCodec<ExtremeCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtremeCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        /**
         * Decode a recipe from network buffer (client receives it).
         */
        private static ExtremeCraftingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            ExtremeCraftingPattern pattern = ExtremeCraftingPattern.STREAM_CODEC.decode(buf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            return new ExtremeCraftingRecipe(pattern, output);
        }

        /**
         * Encode a recipe into network buffer (server sends it).
         */
        private static void toNetwork(RegistryFriendlyByteBuf buf, ExtremeCraftingRecipe recipe) {
            ExtremeCraftingPattern.STREAM_CODEC.encode(buf, recipe.pattern());
            ItemStack.STREAM_CODEC.encode(buf, recipe.output());
        }

        //I wanna kms





    }
}