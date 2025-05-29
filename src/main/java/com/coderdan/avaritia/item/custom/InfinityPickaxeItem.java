package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.datagen.ModTags;
import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.ModItemProperties;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import com.coderdan.avaritia.item.utils.ModItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class InfinityPickaxeItem extends PickaxeItem {
    public InfinityPickaxeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }



    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {

        if (!pTarget.level().isClientSide()) {
            // Try to kill normally via massive damage
            pTarget.invulnerableTime = 0; // bypass hurt cooldown

            DamageSource infinityDamage = pTarget.damageSources().playerAttack((Player) pAttacker);

            pTarget.hurt(infinityDamage, Integer.MAX_VALUE); // simulate unstoppable damage

            // Fallback: if somehow still alive, force kill
            if (pTarget.isAlive()) {
                pTarget.kill();
            }

        }

        pStack.setDamageValue(0);

        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.isCrouching()) {
            // Toggle logic
            if (stack.has(ModDataComponentTypes.IS_WORLD_BREAKING.get())) {
                stack.remove(ModDataComponentTypes.IS_WORLD_BREAKING.get());
                System.out.println("Removed used");
            } else {
                stack.set(ModDataComponentTypes.IS_WORLD_BREAKING.get(), BlockPos.ZERO); // any valid BlockPos is fine
                System.out.println("Added used");
            }

            return InteractionResultHolder.success(stack);
        }


        return super.use(pLevel, pPlayer, pUsedHand);
    }


    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pMiningEntity) {



        if (pLevel.isClientSide()) return false;
        if (!(pMiningEntity instanceof Player player)) return false;

        if (pStack.has(ModDataComponentTypes.IS_WORLD_BREAKING.get())) {
            List<ItemStack> collected = new java.util.ArrayList<>();

            BlockPos.betweenClosedStream(pPos.offset(-8, -8, -8), pPos.offset(8, 8, 8)).forEach(targetPos -> {
                if (!targetPos.equals(pPos)) {
                    BlockState targetState = pLevel.getBlockState(targetPos);
                    if (!targetState.isAir() && targetState.getDestroySpeed(pLevel, targetPos) != -1) {
                        // Simulate block drops
                        List<ItemStack> drops = Block.getDrops(
                                targetState,
                                (net.minecraft.server.level.ServerLevel) pLevel,
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

            // Only drop a Matter Cluster if anything was collected
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

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);

        Holder<Enchantment> fortune = Enchantments.FORTUNE.getOrThrow(Minecraft.getInstance().level);
        pStack.enchant(fortune, 10);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return false;
    }
}



