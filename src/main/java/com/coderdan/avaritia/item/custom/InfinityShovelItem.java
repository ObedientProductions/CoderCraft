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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InfinityShovelItem extends ShovelItem {
    public InfinityShovelItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if(pPlayer.isCrouching())
        {
            // Toggle logic
            if (stack.has(ModDataComponentTypes.IS_WORLD_BREAKING.get())) {
                stack.remove(ModDataComponentTypes.IS_WORLD_BREAKING.get());
                System.out.println("Removed used");
            } else {
                stack.set(ModDataComponentTypes.IS_WORLD_BREAKING.get(), BlockPos.ZERO); // any valid BlockPos is fine
                System.out.println("Added used");
            }

            return  InteractionResultHolder.success(stack);
        }


        return super.use(pLevel, pPlayer, pUsedHand);
    }


    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pMiningEntity) {

        if (pLevel.isClientSide()) return false;
        if (!(pMiningEntity instanceof Player player)) return false;

        if (pStack.has(ModDataComponentTypes.IS_WORLD_BREAKING.get())) {
            List<ItemStack> collected = new ArrayList<>();

            BlockPos.betweenClosedStream(pPos.offset(-8, -8, -8), pPos.offset(8, 8, 8)).forEach(targetPos -> {
                if (!targetPos.equals(pPos)) {
                    BlockState targetState = pLevel.getBlockState(targetPos);

                    if (
                            !targetState.isAir() &&
                                    targetState.getDestroySpeed(pLevel, targetPos) != -1 &&
                                    targetState.is(BlockTags.MINEABLE_WITH_SHOVEL)
                    ) {
                        List<ItemStack> drops = Block.getDrops(
                                targetState,
                                (ServerLevel) pLevel,
                                targetPos,
                                pLevel.getBlockEntity(targetPos),
                                player,
                                pStack
                        );

                        collected.addAll(drops);
                        pLevel.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());
                    }
                }
            });

            ModItemUtils.dropMatterCluster(pLevel, player, collected);
            return true;
        }


        return super.mineBlock(pStack, pLevel, pState, pPos, pMiningEntity);
    }


    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§c" + translated);
    }
}


