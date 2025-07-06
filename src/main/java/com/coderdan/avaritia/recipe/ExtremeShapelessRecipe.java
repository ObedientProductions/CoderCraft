package com.coderdan.avaritia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record ExtremeShapelessRecipe(
        List<Ingredient> ingredients,
        ItemStack output
) implements Recipe<ExtremeCraftingRecipeInput> {

    @Override
    public boolean matches(ExtremeCraftingRecipeInput input, Level level) {
        if (level.isClientSide()) return false;

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) inputs.add(stack.copy());
        }

        // Fail if counts don't match exactly
        if (inputs.size() != ingredients.size()) return false;

        // Track unmatched ingredients
        List<Ingredient> unmatched = new ArrayList<>(ingredients);

        for (ItemStack stack : inputs) {
            boolean matched = false;
            for (int i = 0; i < unmatched.size(); i++) {
                if (unmatched.get(i).test(stack)) {
                    unmatched.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false; // item didn't match any remaining ingredient
        }

        return unmatched.isEmpty();
    }



    @Override
    public ItemStack assemble(ExtremeCraftingRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipies.EXTREMECRAFTING_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipies.EXTREMECRAFTING_SHAPELESS_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ExtremeShapelessRecipe> {

        // JSON reading support
        public static final MapCodec<ExtremeShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(ExtremeShapelessRecipe::ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(ExtremeShapelessRecipe::output)
        ).apply(instance, ExtremeShapelessRecipe::new));

        // Network support
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtremeShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<ExtremeShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtremeShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ExtremeShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            return new ExtremeShapelessRecipe(ingredients, output);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, ExtremeShapelessRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
        }
    }
}
