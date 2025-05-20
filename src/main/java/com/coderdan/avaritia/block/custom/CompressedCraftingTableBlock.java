package com.coderdan.avaritia.block.custom;

import com.coderdan.avaritia.block.entity.custom.CompressedCraftingTableBlockEntity;
import com.coderdan.avaritia.screen.custom.CompressedCraftingMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CompressedCraftingTableBlock extends Block {

    public CompressedCraftingTableBlock(Properties p_49795_) {
        super(p_49795_);
    }


    public static final MapCodec<CraftingTableBlock> CODEC = simpleCodec(CraftingTableBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    @Override
    public MapCodec<? extends CraftingTableBlock> codec() {
        return CODEC;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
            pPlayer.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider(
                (p_52229_, p_52230_, p_52231_) -> new CompressedCraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(pLevel, pPos)), CONTAINER_TITLE
        );
    }
}
