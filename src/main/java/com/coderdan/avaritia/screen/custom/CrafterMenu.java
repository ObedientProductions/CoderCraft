package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.block.entity.custom.CrafterBlockEntity;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrafterMenu extends AbstractContainerMenu {

    public final CrafterBlockEntity blockEntity;
    private final Level level;


    public CrafterMenu(int pContainerId, Inventory pInv, FriendlyByteBuf pExtraData) {
        this(pContainerId, pInv, pInv.player.level().getBlockEntity(pExtraData.readBlockPos()));
    }

    public CrafterMenu(int pContainerId, Inventory pInv, BlockEntity blockEntity) {
        super(ModMenuTypes.CRAFTER_MENU.get(), pContainerId);
        this.blockEntity = (CrafterBlockEntity) blockEntity;
        this.level = pInv.player.level();

        addPlayerInventory(pInv);
        addPlayerHotbar(pInv);

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80-18, 35-18));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 80, 35-18));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 2, 80+18, 35-18));

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 3, 80, 35));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 4, 80+18, 35));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 5, 80-18, 35));

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 6, 80-18, 35+18));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 7, 80, 35+18));
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 8, 80+18, 35+18));

        ModNonHighlightableSlotHandler outputSlotHandler = new ModNonHighlightableSlotHandler(this.blockEntity.inventory, 9, 80 + 69, 35);
        outputSlotHandler.setGhostItem(new ItemStack(ModItems.SPECIAL_ITEM.get()));

        this.addSlot(outputSlotHandler);

    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 9;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.CRAFTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, (84 + i * 18) + 10));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + 10));
        }
    }
}
