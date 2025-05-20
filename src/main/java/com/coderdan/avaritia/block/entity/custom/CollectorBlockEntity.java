package com.coderdan.avaritia.block.entity.custom;

import com.coderdan.avaritia.entity.ModBlockEntities;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.custom.NeutronPileItem;
import com.coderdan.avaritia.recipe.*;
import com.coderdan.avaritia.screen.custom.CollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollectorBlockEntity extends BlockEntity implements MenuProvider {

    public CollectorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COLLECTOR.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Neutronium Collector");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CollectorMenu(pContainerId, pPlayerInventory, this);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(81){

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

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    };


    //to sync

    float progress = 0;

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", inventory.serializeNBT(pRegistries));
        if(progress != 0) pTag.putFloat("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inventory.deserializeNBT(pRegistries, pTag.getCompound("inventory"));

        this.progress = pTag.getFloat("progress");
    }





    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float v)
    {
        this.progress = v;
    }







    public void tick() {
        if (level == null || level.isClientSide) return;

        //inventory.setStackInSlot(0, new ItemStack(Items.IRON_INGOT));



        ItemStack output = inventory.getStackInSlot(0);
        boolean isWorking = false;

        if (output.isEmpty() || output.getCount() < output.getMaxStackSize()) {

            progress += 0.015f; // //0.015f
            isWorking = true;


            if (progress >= 100f) {
                progress = 0;


                ItemStack neutronPile = new ItemStack(ModItems.PILE_OF_NEUTRONS.get());
                if (output.isEmpty()) {
                    inventory.setStackInSlot(0, neutronPile);
                } else{
                    output.grow(1);
                    inventory.setStackInSlot(0, output); // re-set grown stack
                }

                setChanged();
            }
        }


        BooleanProperty LIT = BlockStateProperties.LIT;
        BlockState currentState = level.getBlockState(worldPosition);
        if (currentState.getValue(LIT) != isWorking) {
            level.setBlock(worldPosition, currentState.setValue(LIT, isWorking), 3);
        }
    }

    private final LazyOptional<ItemStackHandler> lazyInventory = LazyOptional.of(() -> inventory);


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyInventory.cast();
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
}
