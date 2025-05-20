package com.coderdan.avaritia.block.entity.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class SidedHandler implements IItemHandler {

    private final ItemStackHandler inventory; // <-- need this
    private final boolean allowInsert;

    public SidedHandler(ItemStackHandler inventory, boolean allowInsert) {
        this.inventory = inventory;
        this.allowInsert = allowInsert;
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (allowInsert && slot == 0) {
            return inventory.insertItem(slot, stack, simulate);
        }
        return stack; // block
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!allowInsert && slot == 1) {
            return inventory.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY; // block
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (allowInsert) {
            return slot == 0;
        }
        return false;
    }
}
