package com.coderdan.avaritia.screen.custom;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ModNonHighlightableSlotHandler extends ModNoneHightlightableSlot {

    private static Container emptyInventory = new SimpleContainer(0);
    private final IItemHandler itemHandler;
    private final int index;

    protected ItemStack ghostItem;


    public ModNonHighlightableSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public void setGhostItem(ItemStack item){
        this.ghostItem = item;
    }

    public ItemStack getGhostItem() {
        return this.ghostItem;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
    {
        return false;
    }

    @Override
    @NotNull
    public ItemStack getItem()
    {
        if(this.ghostItem == null)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            return this.ghostItem;
        }
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void set(@NotNull ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
        this.setChanged();
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    // @Override
    public void initialize(ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
        this.setChanged();
    }

    @Override
    public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn)
    {

    }

    @Override
    public int getMaxStackSize()
    {
        return this.itemHandler.getSlotLimit(this.index);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(index);
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;

            handlerModifiable.setStackInSlot(index, ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(index, maxAdd, true);

            handlerModifiable.setStackInSlot(index, currentStack);

            return maxInput - remainder.getCount();
        }
        else
        {
            ItemStack remainder = handler.insertItem(index, maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }





    @Override
    public boolean mayPickup(Player playerIn)
    {
        return false;
    }

    @Override
    @NotNull
    public ItemStack remove(int amount)
    {
        return this.getItemHandler().extractItem(index, amount, false);
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }


}
