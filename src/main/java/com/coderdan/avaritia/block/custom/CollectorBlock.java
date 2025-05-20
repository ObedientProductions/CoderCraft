package com.coderdan.avaritia.block.custom;

import com.coderdan.avaritia.block.entity.custom.CollectorBlockEntity;
import com.coderdan.avaritia.entity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CollectorBlock extends AbstractFurnaceBlock {


    public static final MapCodec<? extends AbstractFurnaceBlock> CODEC = simpleCodec(CollectorBlock::new);

    public CollectorBlock(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CollectorBlockEntity(pPos, pState);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {

    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        CollectorBlockEntity collector = (CollectorBlockEntity) pLevel.getBlockEntity(pPos);
        boolean currentState = pState.getValue(LIT);

        Direction facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos adjacentPos = pPos.relative(facing);

        //pLevel.playSound(pPlayer, pPos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.4f,1);

        if(!pLevel.isClientSide()) {
            ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(collector, Component.literal("Neutronium Collector")), pPos);
            return ItemInteractionResult.SUCCESS;
        }


        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }



    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntities.COLLECTOR.get()
                ? (lvl, pos, blockState, blockEntity) -> ((CollectorBlockEntity) blockEntity).tick()
                : null;
    }


    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CollectorBlockEntity collector) {
                SimpleContainer container = new SimpleContainer(collector.inventory.getSlots());
                for (int i = 0; i < collector.inventory.getSlots(); i++) {
                    container.setItem(i, collector.inventory.getStackInSlot(i));
                }
                Containers.dropContents(pLevel, pPos, container);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
