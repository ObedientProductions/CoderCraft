package com.coderdan.compact;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.recipe.CollectorRecipeDisplay;
import com.coderdan.avaritia.recipe.CompressorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CollectorRecipeCatagory implements IRecipeCategory<CollectorRecipeDisplay> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "collection_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/neutron_collector/neutron_collector_gui.png");

    public static RecipeType<CollectorRecipeDisplay> COLLECTION_RECIPE_TYPE = new RecipeType<>(UID, CollectorRecipeDisplay.class);

    private final IDrawable background;
    private final IDrawable icon;



    public CollectorRecipeCatagory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 75,30,26,26);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COLLECTOR.get()));
    }

    @Override
    public RecipeType<CollectorRecipeDisplay> getRecipeType() {
        return COLLECTION_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.avaritia.neutronium_collection");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CollectorRecipeDisplay recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 5, 5).addItemStack(recipe.output());
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }


}
