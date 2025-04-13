package com.coderdan.codercraft.block.custom;

import com.coderdan.codercraft.block.entity.custom.CrafterBlockEntity;
import com.coderdan.codercraft.sound.ModSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CrafterBlock extends AbstractFurnaceBlock {

    public static int GLOWTICKS = 6;
    public static final IntegerProperty TICKSPROP = IntegerProperty.create("ticks", 0, GLOWTICKS);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TICKSPROP);
    }

    public static final MapCodec<? extends AbstractFurnaceBlock> CODEC = simpleCodec(CrafterBlock::new);

    public CrafterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }


    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrafterBlockEntity(pPos, pState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        CrafterBlockEntity crafter = (CrafterBlockEntity) pLevel.getBlockEntity(pPos);
        boolean currentState = pState.getValue(LIT);

        Direction facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos adjacentPos = pPos.relative(facing);

        if(!pStack.isEmpty() || !crafter.inventory.getStackInSlot(0).isEmpty())
        {
            for (int i = 0; i < crafter.inventory.getSlots(); i++) {
                if (crafter.inventory.getStackInSlot(i).isEmpty())
                {
                    crafter.inventory.insertItem(0, pStack.copy(), false);
                    pStack.shrink(pStack.getCount());
                    pLevel.playSound(pPlayer, pPos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.4f,1);
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, !currentState));
                    return ItemInteractionResult.SUCCESS;

                }
                else
                {
                    pLevel.playSound(pPlayer, pPos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.4f,1);

                    for (int j = 0; j < crafter.inventory.getSlots(); j++) {

                        Containers.dropItemStack(pLevel, adjacentPos.getX(), adjacentPos.getY(), adjacentPos.getZ(), crafter.inventory.getStackInSlot(j));
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, !currentState));
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }
        else
        {
            for (int k = 0; k < crafter.inventory.getSlots(); k++) {
                Containers.dropItemStack(pLevel, adjacentPos.getX(), adjacentPos.getY(), adjacentPos.getZ(), crafter.inventory.getStackInSlot(k));
                pLevel.playSound(pPlayer, pPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.25f,0.9f);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {

        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CrafterBlockEntity crafter) {
                for (int i = 0; i < crafter.inventory.getSlots(); i++) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY() + 1, pPos.getZ(), crafter.inventory.getStackInSlot(i));
                }
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }


    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        boolean currentState = pState.getValue(LIT);
        int ticks = pState.getValue(TICKSPROP);


        if(!pLevel.isClientSide())
        {

            if(ticks <= GLOWTICKS - 1 && currentState)
            {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(TICKSPROP, ticks + 1));
            }
            else
            {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false).setValue(TICKSPROP, 0));
            }

            //pLevel.players().forEach(p -> p.sendSystemMessage(Component.literal("ticks " + ticks + "  " + currentState)));
        }

        pLevel.scheduleTick(pPos, this, 1); // Reschedule
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        pLevel.scheduleTick(pPos, this, 1); // Reschedule
    }
}

