package com.coderdan.avaritia.block.custom;

import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TransmuterBlock extends Block {

    public static int GLOWTICKS = 6;
    protected static Float JUMPHEIGHT = 0.2f; // very sensitive

    public static final BooleanProperty CLICKED = BooleanProperty.create("clicked");
    public static final IntegerProperty TICKSPROP = IntegerProperty.create("ticks", 0, GLOWTICKS);


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CLICKED, TICKSPROP);
    }



    public TransmuterBlock(Properties p) {
        super(p);
        this.registerDefaultState(this.defaultBlockState().setValue(CLICKED, false));
        this.registerDefaultState(this.defaultBlockState().setValue(TICKSPROP, 0));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {

        pLevel.playSound(pPlayer, pPos, ModSounds.TRANSMUTER_BLOCK_USE.get(), SoundSource.BLOCKS, 1f, 1f);


        return InteractionResult.SUCCESS;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {




        super.stepOn(pLevel, pPos, pState, pEntity);
    }


    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        boolean currentState = pState.getValue(CLICKED);
        int ticks = pState.getValue(TICKSPROP);


        if(!pLevel.isClientSide())
        {

            if(ticks <= GLOWTICKS - 1 && currentState)
            {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(TICKSPROP, ticks + 1));
            }
            else
            {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(CLICKED, false).setValue(TICKSPROP, 0));
            }

            // npLevel.players().forEach(p -> p.sendSystemMessage(Component.literal("ticks " + ticks + "  " + currentState)));
        }

        pLevel.scheduleTick(pPos, this, 1); // Reschedule
    }


    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);

        pLevel.scheduleTick(pPos, this, 1); // Reschedule
    }


}
