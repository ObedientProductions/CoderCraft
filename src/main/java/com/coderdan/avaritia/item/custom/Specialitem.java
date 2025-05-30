package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.projectiles.ThrownEndestpearlProjectile;
import com.coderdan.avaritia.item.projectiles.ThrownSpecialItemProjectile;
import com.coderdan.avaritia.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Map;
import java.util.function.Consumer;

public class Specialitem extends TranslatableItem {
    public Specialitem(Properties pProperties) {
        super(pProperties);
    }

    private static final Map<Block, Block> SPECIAL_MAP = Map.of(
            Blocks.STONE, Blocks.STONE_BRICKS,
            Blocks.END_STONE, Blocks.END_STONE_BRICKS,
            Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS,
            Blocks.IRON_BLOCK, Blocks.DIAMOND_BLOCK
    );

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound(
                null,
                pPlayer.getX(),
                pPlayer.getY(),
                pPlayer.getZ(),
                ModSounds.QUACK.get(),
                SoundSource.NEUTRAL,
                0.5F,
                1f
        );
        pPlayer.getCooldowns().addCooldown(this, 1);
        if (!pLevel.isClientSide) {
            ThrownSpecialItemProjectile thrownPearl = new ThrownSpecialItemProjectile(pLevel, pPlayer);
            thrownPearl.setItem(itemstack);
            thrownPearl.setInvulnerable(true);
            thrownPearl.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(thrownPearl);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        itemstack.consume(1, pPlayer);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }


    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§6" + translated);
    }
}
