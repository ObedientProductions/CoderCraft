package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.entity.ModEntities;
import com.coderdan.avaritia.entity.custom.InfinityArrowEntity;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class InfinityBowItem extends BowItem {
    public InfinityBowItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return stack -> stack.is(ModItems.INFINITY_ARROW.get());
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> true;
    }

    @Override
    public ItemStack getDefaultInstance() {
        return new ItemStack(ModItems.INFINITY_ARROW.get());
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int charge = getUseDuration(pStack, entity) - timeLeft;
        float power = getPowerForTime(charge * 3);



        if (power < 0.1f) return;

        if (!level.isClientSide) {
            // Always use your custom arrow
            InfinityArrowEntity arrow = new InfinityArrowEntity(level, player, ModItems.INFINITY_ARROW.get());
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 0.0F);
            arrow.setBaseDamage(Integer.MAX_VALUE);
            arrow.setCritArrow(false);

            level.addFreshEntity(arrow);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.random.nextFloat() * 0.4F + 1.2F));

        player.getCooldowns().addCooldown(this, 2);

    }


    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!(pLivingEntity instanceof Player player)) return;

        int maxUse = this.getUseDuration(pStack, pLivingEntity);
        int charge = maxUse - pRemainingUseDuration;

        // Speed up charging: simulate faster draw
        float power = BowItem.getPowerForTime(charge * 3); // 3x faster

        if (power >= 1.0f) {
            this.releaseUsing(pStack, player.level(), player, pRemainingUseDuration);
            player.releaseUsingItem();
        }
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
