package com.coderdan.avaritia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ExtremeCraftingRecipeInput(ItemStack[] inputs) implements RecipeInput {

    /**
     * Returns the ItemStack at the given slot index.
     *
     * This is called during recipe matching to check
     * if the items in the crafting grid match the recipe pattern.
     *
     * IMPORTANT:
     * - If the index is out of bounds, it returns an EMPTY item instead of crashing.
     * - This is important because during matching Minecraft might ask for invalid slots sometimes.
     */
    @Override
    public ItemStack getItem(int pIndex) {
        if (pIndex < 0 || pIndex >= inputs.length) {
            return ItemStack.EMPTY;
        }
        return inputs[pIndex];
    }

    /**
     * Returns how many slots are in the input.
     *
     * In our case, it should always be 81 (for a 9x9 grid),
     * because we designed ExtremeCrafting to always simulate a full 9x9 crafting table.
     */
    @Override
    public int size() {
        return inputs.length;
    }
}
