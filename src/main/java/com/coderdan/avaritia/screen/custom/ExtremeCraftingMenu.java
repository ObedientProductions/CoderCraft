package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.block.entity.custom.ExtremeCraftingTableBlockEntity;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipe;
import com.coderdan.avaritia.recipe.ExtremeCraftingRecipeInput;
import com.coderdan.avaritia.recipe.ModRecipies;
import com.coderdan.avaritia.screen.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class ExtremeCraftingMenu extends AbstractContainerMenu {

    public final ExtremeCraftingTableBlockEntity blockEntity;
    private final Level level;

    private boolean wasCraftingLastTick = false;


    ContainerData data;

    public ExtremeCraftingMenu(int pContainerId, Inventory pInv, FriendlyByteBuf pExtraData) {
        this(pContainerId, pInv, pInv.player.level().getBlockEntity(pExtraData.readBlockPos()));
    }

    public ExtremeCraftingMenu(int pContainerId, Inventory pInv, BlockEntity blockEntity){
        super(ModMenuTypes.EXTREME_CRAFTING.get(), pContainerId);

        this.blockEntity = (ExtremeCraftingTableBlockEntity) blockEntity;
        this.level = pInv.player.level();
        this.blockEntity.setMenu(this);


        addPlayerInventory(pInv, 31, 90);
        addPlayerHotbar(pInv, 31,90);

        addCraftingGridSlots(12,8,18);

        //output slot
        this.addSlot(new ModOutputSlot(this.blockEntity.inventory, 81, 210, 80)); //output
        System.out.println("slot " + 81 + " generated");



    }






    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 82;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);

        if (sourceSlot instanceof ModNonHighlightableSlotHandler) {
            System.out.println("Blocked shift-click from ghost slot");
            return ItemStack.EMPTY;
        }

        //IF SHIFT CLICK OUTPUT SLOT
        if (pIndex == 117) {
            ItemStack[] inputs = new ItemStack[81];
            for (int i = 0; i < 81; i++) {
                inputs[i] = blockEntity.inventory.getStackInSlot(i).copy();
            }

            ExtremeCraftingRecipeInput input = new ExtremeCraftingRecipeInput(inputs);
            Optional<RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>> recipeOpt = findExtremeRecipe(input);




            if (recipeOpt.isEmpty()) return ItemStack.EMPTY;

            ItemStack result = recipeOpt.get().value().assemble(input, level.registryAccess());
            if (result.isEmpty()) return ItemStack.EMPTY;

            int maxCrafts = Integer.MAX_VALUE;
            for (ItemStack stack : inputs) {
                if (!stack.isEmpty()) {
                    maxCrafts = Math.min(maxCrafts, stack.getCount());
                }
            }

            if (maxCrafts == 0 || maxCrafts == Integer.MAX_VALUE) return ItemStack.EMPTY;

            ItemStack totalCrafted = result.copy();
            totalCrafted.setCount(result.getCount() * maxCrafts);

            boolean moved = moveItemStackTo(totalCrafted, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false);
            if (!moved) return ItemStack.EMPTY;

            for (int i = 0; i < 81; i++) {
                ItemStack inputStack = blockEntity.inventory.getStackInSlot(i);
                if (!inputStack.isEmpty()) {
                    inputStack.shrink(maxCrafts);
                    blockEntity.inventory.setStackInSlot(i, inputStack);
                }
            }

            // Recheck after consuming inputs
            ItemStack[] newInputs = new ItemStack[81];
            for (int i = 0; i < 81; i++) {
                newInputs[i] = blockEntity.inventory.getStackInSlot(i);
            }
            ExtremeCraftingRecipeInput newInput = new ExtremeCraftingRecipeInput(newInputs);
            Optional<RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>> newRecipe = findExtremeRecipe(input);


            if (newRecipe.isPresent()) {
                ItemStack newResult = newRecipe.get().value().assemble(newInput, level.registryAccess());
                blockEntity.inventory.setStackInSlot(81, newResult);
            } else {
                blockEntity.inventory.setStackInSlot(81, ItemStack.EMPTY);
            }


            wasCraftingLastTick = false;
            serverTick();

            return totalCrafted;
        }


        // NORMAL SLOT HANDLING
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackToFiltered(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);


        this.broadcastChanges();

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

    private void addCraftingGridSlots(int startX, int startY, int slotSize) {
        //startX Starting X (where your first slot is)
        //startY Starting Y (where your first slot is)
        //slotSize Slot size (vanilla is 18x18 pixels)

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col; // Calculate inventory slot index
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                this.addSlot(new SlotItemHandler(this.blockEntity.inventory, index, x, y));
                System.out.println("slot " + index + " generated");
            }
        }
    }







    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.EXTREME_CRAFTING_TABLE.get());
    }

    private void addPlayerInventory(Inventory playerInventory, int offsetX, int offsetY) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, (8 + l * 18) + offsetX, (84 + i * 18) + offsetY));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory, int offsetX, int offsetY) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, (8 + i * 18) + offsetX, 142 + offsetY));
        }
    }


    public void serverTick() {
        if (level == null || level.isClientSide) return;

        ItemStack[] inputs = new ItemStack[81];
        for (int i = 0; i < 81; i++) {
            inputs[i] = blockEntity.inventory.getStackInSlot(i);
        }
        ExtremeCraftingRecipeInput input = new ExtremeCraftingRecipeInput(inputs);

        Optional<RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>> recipeOpt = findExtremeRecipe(input);


        if (recipeOpt.isPresent()) {
            ItemStack expectedResult = recipeOpt.get().value().assemble(input, level.registryAccess());

            if (expectedResult.getItem() == ModItems.INFINITY_PICKAXE.get()) {
                Holder<Enchantment> fortune = Enchantments.FORTUNE.getOrThrow(blockEntity.getLevel());
                expectedResult.enchant(fortune, 10);
            }


            ItemStack currentOutput = blockEntity.inventory.getStackInSlot(81);

            if (wasCraftingLastTick && currentOutput.isEmpty()) {
                for (int i = 0; i < 81; i++) {
                    ItemStack slotStack = blockEntity.inventory.getStackInSlot(i);
                    if (!slotStack.isEmpty()) {
                        slotStack.shrink(1);
                        blockEntity.inventory.setStackInSlot(i, slotStack);
                        blockEntity.setChanged();

                        this.broadcastChanges();
                    }
                }
            }

            blockEntity.inventory.setStackInSlot(81, expectedResult);
            wasCraftingLastTick = true;
            blockEntity.setChanged();

        } else {
            if (!blockEntity.inventory.getStackInSlot(81).isEmpty()) {
                blockEntity.inventory.setStackInSlot(81, ItemStack.EMPTY);
                blockEntity.setChanged();
            }
            wasCraftingLastTick = false;
        }




    }


    @SuppressWarnings("unchecked")
    private Optional<RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>> findExtremeRecipe(ExtremeCraftingRecipeInput input) {
        Optional<? extends RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>> opt =
                (Optional) level.getRecipeManager().getRecipeFor(ModRecipies.EXTREMECRAFTING_TYPE.get(), input, level);

        if (opt.isEmpty()) {
            opt = (Optional) level.getRecipeManager().getRecipeFor(ModRecipies.EXTREMECRAFTING_SHAPELESS_TYPE.get(), input, level);
        }

        return (Optional<RecipeHolder<? extends Recipe<ExtremeCraftingRecipeInput>>>) opt;
    }





}
