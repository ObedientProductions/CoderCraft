package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import com.coderdan.avaritia.item.utils.ModItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InfinityAxeItem extends AxeItem {
    public InfinityAxeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide() && pPlayer.isCrouching()) {
            BlockPos center = pPlayer.blockPosition();
            List<ItemStack> collected = new ArrayList<>();

            BlockPos.betweenClosedStream(center.offset(-8, -8, -8), center.offset(8, 8, 8)).forEach(pos -> {
                BlockState state = pLevel.getBlockState(pos);
                if (state.isAir()) return;

                boolean shouldDestroy =
                        state.is(BlockTags.LEAVES) ||
                                state.is(BlockTags.LOGS) ||
                                state.is(BlockTags.FLOWERS) ||
                                state.is(Blocks.SHORT_GRASS) ||
                                state.is(Blocks.TALL_GRASS) ||
                                state.is(Blocks.FERN) ||
                                state.is(Blocks.LARGE_FERN) ||
                                state.is(Blocks.SNOW);

                if (shouldDestroy) {
                    List<ItemStack> drops = Block.getDrops(
                            state,
                            (ServerLevel) pLevel,
                            pos,
                            pLevel.getBlockEntity(pos),
                            pPlayer,
                            stack
                    );
                    collected.addAll(drops);
                    pLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                } else if (state.is(Blocks.GRASS_BLOCK)) {
                    pLevel.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                }
            });

            if (!collected.isEmpty()) {
                ModItemUtils.dropMatterCluster(pLevel, pPlayer, collected);
            }

            pPlayer.displayClientMessage(Component.literal("§7Cleared surroundings."), true);
        }

        return InteractionResultHolder.success(stack);
    }
}


