package com.coderdan.compat;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModConfig;
import com.coderdan.avaritia.block.ModBlocks;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CompressorRecipeCatagory implements IRecipeCategory<CompressorRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "compression_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/compressor/compressor.png");

    public static RecipeType<CompressorRecipe> COMPRESSION_RECIPE_TYPE = new RecipeType<>(UID, CompressorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawableStatic arrowStatic;
    //private final IDrawableAnimated arrowAnimated;

    private final IDrawableStatic vortexStatic;
    private final IDrawableAnimated vortexAnimated;


    public CompressorRecipeCatagory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 36,26,104,44);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COMPRESSOR.get()));

        this.arrowStatic = helper.createDrawable(TEXTURE, 176, 0, 22, 16);

        this.vortexStatic = helper.createDrawable(TEXTURE, 176, 16, 16, 16);
        this.vortexAnimated = helper.createAnimatedDrawable(vortexStatic, 160, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<CompressorRecipe> getRecipeType() {
        return COMPRESSION_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.avaritia.neutronium_compression");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompressorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 3,9).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 9).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(CompressorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        int duration = recipe.processDurration();
        if (!ModConfig.ProcessingSpeed.get()) {
            duration = 5; // instant mode, fall back to 5 ticks or skip anim
        }

        int width = arrowStatic.getWidth();
        int height = arrowStatic.getHeight();

        // Time-based anim cycle
        int ticks = (int) (Minecraft.getInstance().level.getGameTime() % duration);
        int frameWidth = (int)((ticks / (float)duration) * width);

        // Draw partial arrow (frameWidth portion)
        guiGraphics.blit(TEXTURE, 26, 9, 176, 0, frameWidth, height);

        vortexAnimated.draw(guiGraphics, 54,9);

        //draw required amount and duration

        int baseCount = recipe.requiredCount();
        double multiplier = ModConfig.singularityDifficulty.get();
        int requiredAmount = (int)(baseCount * multiplier);

        String required = "Input Amount: " + requiredAmount;

        guiGraphics.drawString(Minecraft.getInstance().font, required, 7, 34, 0x404040, false);
    }
}
