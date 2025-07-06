package com.coderdan.avaritia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Input wrapper for shapeless Extreme recipes.
 * Internally holds all non-empty items from the crafting grid.
 */
public class ExtremeShapelessRecipeInput implements RecipeInput {
    private final List<ItemStack> items;

    public ExtremeShapelessRecipeInput(ItemStackHandler handler) {
        if (handler.getSlots() != 81)
            throw new IllegalArgumentException("Expected 81 slots for 9x9 grid, got " + handler.getSlots());
        List<ItemStack> collected = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) collected.add(stack);
        }
        this.items = Collections.unmodifiableList(collected);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) return ItemStack.EMPTY;
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    public List<ItemStack> getItems() {
        return items;
    }
}