package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class InfinitySwordItem extends SwordItem {
    public InfinitySwordItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }


    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pTarget.level().isClientSide()) {
            pTarget.invulnerableTime = 0;

            // Special case: if hitting a dragon part, redirect to the main dragon
            if (pTarget.getType().toString().contains("ender_dragon_part")) {
                // this shouldn't ever happen here directly since LivingEntity check would fail
                // included for completeness
                return true;
            }

            // Check if Ender Dragon (via indirect part hit)
            if (pTarget.getType().toString().contains("ender_dragon")) {
                // Use normal damage logic to let death animation happen
                pTarget.hurt(pTarget.damageSources().playerAttack((Player)pAttacker), pTarget.getHealth());

                // Force the dragon into dying phase (failsafe)
                if (pTarget instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon dragon) {
                    dragon.getPhaseManager().setPhase(net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase.DYING);
                }
            } else {
                // Standard entities - instant kill
                pTarget.hurt(pTarget.damageSources().playerAttack((Player)pAttacker), Integer.MAX_VALUE);
                if (pTarget.isAlive()) {
                    pTarget.kill(); // Fallback in case it's invulnerable to damage
                }
            }
        }

        pStack.setDamageValue(0);
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }


}


