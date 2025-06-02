package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import com.coderdan.avaritia.item.utils.ModItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InfinityHoeItem extends HoeItem {
    public InfinityHoeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }



    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos clickedPos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();

        // Sneaking? Use vanilla hoe behavior
        if (player.isCrouching()) {
            return super.useOn(pContext);
        }

        if (!level.isClientSide()) {
            List<ItemStack> collected = new ArrayList<>();

            BlockPos.betweenClosedStream(clickedPos.offset(-4, 0, -4), clickedPos.offset(4, 0, 4)).forEach(pos -> {
                BlockPos above = pos.above();
                BlockPos twoAbove = above.above();

                BlockState ground = level.getBlockState(pos);
                BlockState top = level.getBlockState(above);
                BlockState upper = level.getBlockState(twoAbove);

                boolean isDirtLike = ground.is(Blocks.DIRT) || ground.is(Blocks.GRASS_BLOCK);

                if (isDirtLike && (level.isEmptyBlock(above) || (!top.isAir() && level.isEmptyBlock(twoAbove)))) {

                    // Break the block above (if it's a single block tall)
                    if (!top.isAir() && level.isEmptyBlock(twoAbove)) {
                        List<ItemStack> drops = Block.getDrops(
                                top,
                                (ServerLevel) level,
                                above,
                                level.getBlockEntity(above),
                                player,
                                stack
                        );
                        collected.addAll(drops);
                        level.setBlockAndUpdate(above, Blocks.AIR.defaultBlockState());
                    }

                    // Replace dirt/grass with farmland
                    List<ItemStack> baseDrops = Block.getDrops(
                            ground,
                            (ServerLevel) level,
                            pos,
                            level.getBlockEntity(pos),
                            player,
                            stack
                    );
                    collected.addAll(baseDrops);
                    level.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState());
                }
            });

            if (!collected.isEmpty()) {
                ModItemUtils.dropMatterCluster(level, player, collected);
            }

            player.displayClientMessage(Component.literal("§7Prepared farming area."), true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§c" + translated);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return false;
    }


}


