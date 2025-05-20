package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.block.entity.custom.CompressorBlockEntity;
import com.coderdan.avaritia.block.entity.custom.CrafterBlockEntity;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class CompressorMenu extends AbstractContainerMenu {

    public final CompressorBlockEntity blockEntity;
    private final Level level;

    ContainerData data;

    public CompressorMenu(int pContainerId, Inventory pInv, FriendlyByteBuf pExtraData) {
        this(pContainerId, pInv, pInv.player.level().getBlockEntity(pExtraData.readBlockPos()));
    }

    public CompressorMenu(int pContainerId, Inventory pInv, BlockEntity blockEntity){
        super(ModMenuTypes.COMPRESSOR.get(), pContainerId);

        this.blockEntity = (CompressorBlockEntity) blockEntity;
        this.level = pInv.player.level();

        addPlayerInventory(pInv);
        addPlayerHotbar(pInv);


        //ghost input
        ModNonHighlightableSlotHandler inputSlotHandler = new ModNonHighlightableSlotHandler(this.blockEntity.inventory, 2, 14, 35);
        inputSlotHandler.setGhostItem(ItemStack.EMPTY); //starts empty
        this.addSlot(inputSlotHandler);



        this.addSlot(new ModCompressorInputSlot(this.blockEntity.inventory, 0, 39, 35)); //input
        this.addSlot(new ModOutputSlot(this.blockEntity.inventory, 1, 117, 35)); //output

        //ghost output
        ModNonHighlightableSlotHandler outputSlotHandler = new ModNonHighlightableSlotHandler(this.blockEntity.inventory, 3, 147, 35);
        outputSlotHandler.setGhostItem(ItemStack.EMPTY);
        this.addSlot(outputSlotHandler);






        this.data = new ContainerData(){

            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ((CompressorBlockEntity) blockEntity).storedItemCount;
                    case 1 -> ((CompressorBlockEntity) blockEntity).requiredAmount;
                    case 2 -> ((CompressorBlockEntity) blockEntity).processDurration;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex == 0) ((CompressorBlockEntity) blockEntity).storedItemCount = pValue;
                if (pIndex == 1) ((CompressorBlockEntity) blockEntity).requiredAmount = pValue;
                if (pIndex == 2) ((CompressorBlockEntity) blockEntity).processDurration = pValue;
            }

            @Override
            public int getCount() {
                return 4;
            }
        };


        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return ((CompressorBlockEntity) blockEntity).getCompressionProgress();
            }

            @Override
            public void set(int pValue) {
                ((CompressorBlockEntity) blockEntity).setCompressionProgress(pValue);
            }
        });



        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return Item.getId(((CompressorBlockEntity) blockEntity).getCachedInputExample().getItem());
            }

            @Override
            public void set(int value) {
                Item item = Item.byId(value);
                if (item != null) {
                    ((CompressorBlockEntity) blockEntity).setCachedInputExample(new ItemStack(item));
                }
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return Item.getId(((CompressorBlockEntity) blockEntity).getCachedOutputExample().getItem());
            }

            @Override
            public void set(int value) {
                Item item = Item.byId(value);
                if (item != null) {
                    ((CompressorBlockEntity) blockEntity).setCachedOutputExample(new ItemStack(item));
                }
            }
        });





        addDataSlots(this.data);

    }




    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!

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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.COMPRESSOR.get());
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
