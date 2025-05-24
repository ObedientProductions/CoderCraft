package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.entity.custom.InfinityArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class InfinityArrowItem extends ArrowItem {

    public final float damage;

    public InfinityArrowItem(Properties pProperties, float damage) {
        super(pProperties);
        this.damage = damage;
    }


    @Override
    public @NotNull AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack bow) {
        InfinityArrowEntity arrow = new InfinityArrowEntity(level, shooter, this);
        arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 3.0F, 1.0F);
        level.addFreshEntity(arrow);

        arrow.setBaseDamage(this.damage);
        return arrow;
    }


    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, LivingEntity owner) {
        return true;
    }
}
