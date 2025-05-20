package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.block.ModBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Map;
import java.util.function.Consumer;

public class Specialitem extends Item {
    public Specialitem(Properties pProperties) {
        super(pProperties);
    }

    private static final Map<Block, Block> SPECIAL_MAP = Map.of(
            Blocks.STONE, Blocks.STONE_BRICKS,
            Blocks.END_STONE, Blocks.END_STONE_BRICKS,
            Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
            Blocks.IRON_BLOCK, Blocks.DIAMOND_BLOCK,
            Blocks.DIRT, ModBlocks.TITANIUM_BLOCK.get()
    );

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        Level level = pContext.getLevel();
        Block clickedBlock = level.getBlockState(pContext.getClickedPos()).getBlock();

        if(SPECIAL_MAP.containsKey(clickedBlock))
        {
            if(!level.isClientSide())
            {
                level.setBlockAndUpdate(pContext.getClickedPos(), SPECIAL_MAP.get(clickedBlock).defaultBlockState());
                
                pContext.getItemInHand().hurtAndBreak(1, ((ServerLevel) level), (ServerPlayer) pContext.getPlayer(),
                        item -> pContext.getPlayer().onEquippedItemBroken(item, EquipmentSlot.MAINHAND));

                level.playSound(null, pContext.getClickedPos(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS);


            }
        }

        return super.useOn(pContext);
    }









}
