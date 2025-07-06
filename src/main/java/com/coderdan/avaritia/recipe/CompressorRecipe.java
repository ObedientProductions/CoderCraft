package com.coderdan.avaritia.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CompressorRecipe (Ingredient inputItem, ItemStack output, int requiredCount, int processDurration) implements Recipe<CompressorRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputItem);
    }

    //read in JSON file --> turns into CompressorRecipe

    @Override
    public boolean matches(CompressorRecipeInput pInput, Level pLevel) {

        if(pLevel.isClientSide())
        {
            return false;
        }

        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(CompressorRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipies.COMPRESSOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipies.COMPRESSOR_TYPE.get();
    }



    public static class Serializer implements RecipeSerializer<CompressorRecipe>{



        public static final MapCodec<CompressorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CompressorRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CompressorRecipe::output),
                Codec.INT.optionalFieldOf("required", 1).forGetter(CompressorRecipe::requiredCount),
                Codec.INT.optionalFieldOf("processDuration", 1).forGetter(CompressorRecipe::requiredCount)
        ).apply(inst, CompressorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CompressorRecipe::inputItem,
                ItemStack.STREAM_CODEC, CompressorRecipe::output,
                ByteBufCodecs.INT, CompressorRecipe::requiredCount,
                ByteBufCodecs.INT, CompressorRecipe::processDurration,
                CompressorRecipe::new);


        @Override
        public MapCodec<CompressorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}