package com.coderdan.avaritia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraftforge.items.ItemStackHandler;

public record ExtremeCraftingRecipeInput(ItemStack[] inputs) implements RecipeInput {


    public ExtremeCraftingRecipeInput(ItemStackHandler handler) {
        this(extractFromHandler(handler)); // delegate to the canonical constructor
    }


    private static ItemStack[] extractFromHandler(ItemStackHandler handler) {
        if (handler.getSlots() != 81)
            throw new IllegalArgumentException("Expected 81 slots for 9x9 grid, got " + handler.getSlots());
        ItemStack[] items = new ItemStack[81];
        for (int i = 0; i < 81; i++) {
            items[i] = handler.getStackInSlot(i);
        }
        return items;
    }

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
