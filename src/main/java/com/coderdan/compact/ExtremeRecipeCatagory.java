package com.coderdan.compact;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.custom.InfinityPickaxeItem;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public class ExtremeRecipeCatagory implements IRecipeCategory<ExtremeCraftingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "extreme_crafting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/extreme_crafting/extreme_jei.png");

    public static RecipeType<ExtremeCraftingRecipe> EXTREMECRAFTING_RECIPE_TYPE = new RecipeType<>(UID, ExtremeCraftingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ExtremeRecipeCatagory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0,0,188,163);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.EXTREME_CRAFTING_TABLE.get()));
    }

    @Override
    public RecipeType<ExtremeCraftingRecipe> getRecipeType() {
        return EXTREMECRAFTING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.avaritia.extreme_crafting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtremeCraftingRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();

        int slotSize = 18;
        int startX = 2;
        int startY = 2;

        // Add input grid
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;


                Ingredient ingredient = ingredients.get(index);
                builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addIngredients(ingredient);

            }
        }

        // Add output slot

        ItemStack outputToDisplay = null;

        if(recipe.getResultItem(null).getItem() instanceof InfinityPickaxeItem)
        {
            ItemStack displayStack = new ItemStack(ModItems.INFINITY_PICKAXE.get());
            Holder<Enchantment> fortune = Enchantments.FORTUNE.getOrThrow(Minecraft.getInstance().level);
            displayStack.enchant(fortune, 10);

            outputToDisplay = displayStack;
        }
        else
        {
            outputToDisplay = recipe.getResultItem(null);
        }


        builder.addSlot(RecipeIngredientRole.OUTPUT, 168, 74)
                .addItemStack(outputToDisplay);

    }

}
