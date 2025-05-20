package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.block.entity.custom.CollectorBlockEntity;
import com.coderdan.avaritia.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.text.DecimalFormat;

public class CollectorMenu extends AbstractContainerMenu {

    public final CollectorBlockEntity blockEntity;
    private final Level level;

    ContainerData data;

    public CollectorMenu(int pContainerId, Inventory pInv, FriendlyByteBuf pExtraData) {
        this(pContainerId, pInv, pInv.player.level().getBlockEntity(pExtraData.readBlockPos()));
    }

    public CollectorMenu(int pContainerId, Inventory pInv, BlockEntity blockEntity){
        super(ModMenuTypes.COLLECTOR.get(), pContainerId);

        this.blockEntity = (CollectorBlockEntity) blockEntity;
        this.level = pInv.player.level();

        addPlayerInventory(pInv);
        addPlayerHotbar(pInv);

        this.addSlot(new ModOutputSlot(this.blockEntity.inventory, 0, 80, 35)); //input

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (int)(((CollectorBlockEntity) blockEntity).getProgress() * 100); // multiply to preserve float precision
            }

            @Override
            public void set(int value) {
                DecimalFormat df = new DecimalFormat("0.#");
                float result = Float.parseFloat(df.format((float) value / 100));
                ((CollectorBlockEntity) blockEntity).setProgress(result); // divide to restore float
            }
        });
    }




    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);

        if (sourceSlot instanceof ModNonHighlightableSlotHandler) {
            System.out.println("Blocked shift-click from ghost slot");
            return ItemStack.EMPTY;
        }


        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackToFiltered(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else{
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }


        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        }else{
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyOfSourceStack;
    }


    private boolean moveItemStackToFiltered(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        for (int i = reverseDirection ? endIndex - 1 : startIndex;
             reverseDirection ? i >= startIndex : i < endIndex;
             i += reverseDirection ? -1 : 1) {

            Slot slot = this.slots.get(i);
            if (slot instanceof ModNonHighlightableSlotHandler) continue;

            if (slot.mayPlace(stack) && moveItemStackTo(stack, i, i + 1, false)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.COLLECTOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (84 + i * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }



}
