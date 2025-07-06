package com.coderdan.avaritia.recipe;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipies {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Avaritia.MOD_ID);
    public static DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Avaritia.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CompressorRecipe>> COMPRESSOR_SERIALIZER = SERIALIZERS.register("compressor", CompressorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<CompressorRecipe>> COMPRESSOR_TYPE = TYPES.register("compressor", () -> new RecipeType<CompressorRecipe>() {
        @Override
        public String toString() {
            return "compressor";
        }
    });

    public static final RegistryObject<RecipeSerializer<ExtremeCraftingRecipe>> EXTREMECRAFTING_SERIALIZER = SERIALIZERS.register("extreme", ExtremeCraftingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ExtremeCraftingRecipe>> EXTREMECRAFTING_TYPE = TYPES.register("extreme", () -> new RecipeType<ExtremeCraftingRecipe>(){

        @Override
        public String toString() {
            return "extreme";
        }
    });

    public static final RegistryObject<RecipeSerializer<ExtremeShapelessRecipe>> EXTREMECRAFTING_SHAPELESS_SERIALIZER = SERIALIZERS.register("extreme_shapeless", ExtremeShapelessRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ExtremeShapelessRecipe>> EXTREMECRAFTING_SHAPELESS_TYPE = TYPES.register("extreme_shapeless", () -> new RecipeType<ExtremeShapelessRecipe>(){

        @Override
        public String toString() {
            return "extreme_shapeless";
        }
    });



    public static void register(IEventBus bus){
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
