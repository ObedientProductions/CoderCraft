package com.coderdan.avaritia.block.entity.custom;

import ca.weblite.objc.Proxy;
import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModConfig;
import com.coderdan.avaritia.entity.ModBlockEntities;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.recipe.CompressorRecipe;
import com.coderdan.avaritia.recipe.CompressorRecipeInput;
import com.coderdan.avaritia.recipe.ModRecipies;
import com.coderdan.avaritia.screen.custom.CompressorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompressorBlockEntity extends BlockEntity implements MenuProvider {

    public CompressorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPRESSOR.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Neutronium Compressor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CompressorMenu(pContainerId, pPlayerInventory, this);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(5){

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return Integer.MAX_VALUE;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };


    //to sync


    public int storedItemCount = 0;
    public int requiredAmount = -69;
    int compressionProgress = 0;
    public int processDurration = 0;

    @Nullable
    private CompressorRecipe lockedRecipe = null;


    @Nullable
    public ItemStack getLockedInputExample() {
        if (lockedRecipe != null) {
            return lockedRecipe.inputItem().getItems().length > 0 ? lockedRecipe.inputItem().getItems()[0] : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public ItemStack getLockedOutputExample() {
        if (lockedRecipe != null) {
            return lockedRecipe.output();
        }
        return ItemStack.EMPTY;
    }



    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    private ItemStack cachedInputExample = ItemStack.EMPTY;
    private ItemStack cachedOutputExample = ItemStack.EMPTY;
    private String lockedRecipeId = "";

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", inventory.serializeNBT(pRegistries));
        pTag.putInt("StoredItemCount", storedItemCount);

        if(requiredAmount != -69) pTag.putInt("requiredAmount", requiredAmount);
        if(compressionProgress != 0) pTag.putInt("compressionProgress", compressionProgress);
        if(processDurration != 0) pTag.putInt("processDurration", processDurration);

        if (!cachedInputExample.isEmpty()) {
            pTag.put("CachedInputExample", cachedInputExample.save(pRegistries));
        }

        if (!cachedOutputExample.isEmpty()) {
            pTag.put("CachedOutputExample", cachedOutputExample.save(pRegistries));
        }

        if (lockedRecipe != null) {
            pTag.putString("LockedRecipeId", lockedRecipeId);
        }

    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inventory.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        this.storedItemCount = pTag.getInt("StoredItemCount");

        requiredAmount = pTag.getInt("requiredAmount");
        compressionProgress = pTag.getInt("compressionProgress");
        processDurration = pTag.getInt("processDurration");

        if (pTag.contains("CachedInputExample")) {
            cachedInputExample = ItemStack.parse(pRegistries, pTag.getCompound("CachedInputExample")).orElse(new ItemStack(Items.BARRIER));
        }

        if (pTag.contains("CachedOutputExample")) {
            cachedOutputExample = ItemStack.parse(pRegistries, pTag.getCompound("CachedOutputExample")).orElse(new ItemStack(Items.BARRIER));
        }

        if (pTag.contains("LockedRecipeId")) {
            lockedRecipeId = pTag.getString("LockedRecipeId");

            if (!lockedRecipeId.isEmpty()) {
                try {
                    ResourceLocation location = ResourceLocation.parse(lockedRecipeId);
                    level.getRecipeManager().byKey(location).ifPresent(recipeHolder -> {
                        if (recipeHolder.value() instanceof CompressorRecipe recipe) {
                            this.lockedRecipe = recipe;
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Failed to parse locked recipe ID: " + lockedRecipeId);
                    // Fallback or clear lockedRecipe if needed
                    lockedRecipe = null;
                }
            }
        }
    }

    public ItemStack getCachedInputExample() {
        return cachedInputExample;
    }

    public ItemStack getCachedOutputExample() {
        return cachedOutputExample;
    }

    public void setCachedInputExample(ItemStack item) {
        cachedInputExample = item;
    }

    public void setCachedOutputExample(ItemStack item) {
        cachedOutputExample = item;
    }




    public ItemStack recipeOutput = ItemStack.EMPTY;
    public ItemStack recipeInput = ItemStack.EMPTY;


    public boolean hasRecipe(){

        Optional<RecipeHolder<CompressorRecipe>> recipe = getCurrentRecipe();

        if(recipe.isEmpty())
        {
            return false;
        }

        recipeOutput = recipe.get().value().output();

        return true;
    }

    Optional<RecipeHolder<CompressorRecipe>> getCurrentRecipe(){
        return this.level.getRecipeManager().getRecipeFor(ModRecipies.COMPRESSOR_TYPE.get(), new CompressorRecipeInput(inventory.getStackInSlot(0)), level);
    }



    public int getCompressionProgress() {
        return this.compressionProgress;
    }

    public void setCompressionProgress(int v)
    {
        this.compressionProgress = v;
    }



    public void tick() {
        if (level == null || level.isClientSide) return;

        // Auto-restore lockedRecipe from ID if it exists but recipe is missing
        if (lockedRecipe == null && !lockedRecipeId.isEmpty()) {
            try {
                ResourceLocation location = ResourceLocation.parse(lockedRecipeId);
                level.getRecipeManager().byKey(location).ifPresent(recipeHolder -> {
                    if (recipeHolder.value() instanceof CompressorRecipe recipe) {
                        this.lockedRecipe = recipe;
                    } else {
                        // If for some reason it's gone (like missing datapack), clear
                        lockedRecipeId = "";
                    }
                });
            } catch (Exception e) {
                System.err.println("Failed to parse locked recipe ID during tick: " + lockedRecipeId);
                lockedRecipeId = "";
            }
        }

        System.out.println(lockedRecipeId);


        cachedInputExample = ItemStack.EMPTY;
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);

        Optional<RecipeHolder<CompressorRecipe>> recipeOpt = getCurrentRecipe();

        // Handle invalid input first if we have a lockedRecipe
        if (lockedRecipe != null && !lockedRecipe.inputItem().test(input)) {
            compressionProgress = 0;
            // Update lit state
            BooleanProperty LIT = BlockStateProperties.LIT;
            BlockState currentState = level.getBlockState(worldPosition);
            level.setBlock(worldPosition, currentState.setValue(LIT, false), 3);

            setChanged();
            return;
        }




        if (recipeOpt.isEmpty()) {
            lockedRecipe = null;
            if (storedItemCount > 0) return;
            lockedRecipeId = "";
            return;
        }



        CompressorRecipe currentRecipe = recipeOpt.get().value();


        // If no recipe locked yet, lock this one
        if (lockedRecipe == null) {
            lockedRecipe = currentRecipe;
            lockedRecipeId = recipeOpt.get().id().toString();

        }


        ItemStack expectedOutput = lockedRecipe.output();


        int baseCount = lockedRecipe.requiredCount();
        double multiplier = ModConfig.singularityDifficulty.get();
        this.requiredAmount = (int)(baseCount * multiplier);


        this.processDurration = ModConfig.ProcessingSpeed.get() ? lockedRecipe.processDurration() : 0;

        // If input does NOT match locked recipe ingredient, pause
        if (!lockedRecipe.inputItem().test(input)) {
            return;
        }

        // If lockedRecipe exists but is different from current recipe, pause
        String currentId = recipeOpt.get().id().toString();
        if (!currentId.equals(lockedRecipeId)) {
            lockedRecipe = null; // unlock it
            lockedRecipeId = "";
            return;
        }



        // Output slot must be empty or matching the expected output
        if (!output.isEmpty() && !ItemStack.isSameItem(output, expectedOutput)) {
            return; // Output is wrong -> pause, don't compress
        }


        cachedInputExample = lockedRecipe.inputItem().getItems()[0].copy();

        cachedOutputExample = expectedOutput;
        setChanged();





        // store output for GUI
        this.recipeOutput = expectedOutput;
        this.recipeInput = input;



        // only absorb input if valid and not done
        if ((!input.isEmpty() && currentRecipe.inputItem().test(input) && storedItemCount < requiredAmount)) {

            // Phase 1: build compression progress
            compressionProgress++;

            // Update lit state
            BooleanProperty LIT = BlockStateProperties.LIT;
            boolean shouldBeLit = compressionProgress > 0;
            BlockState currentState = level.getBlockState(worldPosition);
            if (currentState.getValue(LIT) != shouldBeLit) {
                level.setBlock(worldPosition, currentState.setValue(LIT, shouldBeLit), 3);
            }

            setChanged();

            if (compressionProgress < processDurration) {
                // Still charging up, no absorption yet
                return;
            }

            // Phase 2: progress complete -> absorb 1 item
            compressionProgress = 0; // reset bar

            inventory.extractItem(0, 1, false);
            storedItemCount += 1;
            setChanged();


            // Sync to client if needed
            if (!level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            if(!(storedItemCount >= requiredAmount))
            {
                return;
            }
        }
        //System.out.println(storedItemCount + "bruh");

        //System.out.println("Triggered");

        // if goal reached
        if (storedItemCount <= requiredAmount) {
            if (output.isEmpty()) {
                inventory.setStackInSlot(1, expectedOutput.copy());
                storedItemCount = 0;
                lockedRecipe = null;
                lockedRecipeId = "";
                setChanged();
            } else if (output.getCount() + expectedOutput.getCount() <= output.getMaxStackSize())
                {
                    if(output.getItem() != expectedOutput.getItem()) return;

                    ItemStack result = expectedOutput.copy();
                    result.setCount(output.getCount() + expectedOutput.getCount());

                    inventory.setStackInSlot(1, result);
                    storedItemCount = 0;
                    lockedRecipe = null;
                    setChanged();
                }
            }
        }


    private final LazyOptional<ItemStackHandler> lazyInventory = LazyOptional.of(() -> inventory);


    private final LazyOptional<IItemHandler> inputHandler = LazyOptional.of(() -> new SidedHandler(inventory, true));
    private final LazyOptional<IItemHandler> outputHandler = LazyOptional.of(() -> new SidedHandler(inventory, false));


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) {
                return outputHandler.cast(); // only allow extracting from output below
            } else {
                return inputHandler.cast();  // allow inserting from sides/top
            }
        }

        return super.getCapability(cap, side);
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInventory.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    //define our own for safety  so display slots are not exposed to other mods and vanilla things

    private static class SidedHandler implements IItemHandler {
        private final ItemStackHandler handler;
        private final boolean isInput;

        public SidedHandler(ItemStackHandler handler, boolean isInput) {
            this.handler = handler;
            this.isInput = isInput;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return handler.getStackInSlot(isInput ? 0 : 1);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!isInput) return stack;
            return handler.insertItem(0, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isInput) return ItemStack.EMPTY;
            return handler.extractItem(1, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return handler.getSlotLimit(isInput ? 0 : 1);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isInput;
        }
    }

}
