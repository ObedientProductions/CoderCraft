package com.coderdan.avaritia.item.projectiles;

import com.coderdan.avaritia.entity.ModEntities;
import com.coderdan.avaritia.entity.custom.GapingVoidEntity;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEndestpearlProjectile extends ThrowableItemProjectile {
    public ThrownEndestpearlProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownEndestpearlProjectile(Level pLevel, LivingEntity pShooter) {
        super(EntityType.ENDER_PEARL, pShooter, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ENDEST_PEARL.get();
    }




    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);

        if(!level().isClientSide)
        {
            level().playSound(null, getX(), getY(), getZ(), ModSounds.GAPING_VOID.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
        }

        if (!(level() instanceof ServerLevel serverLevel)) return;

        // Spawn void entity
        GapingVoidEntity voidSphere = new GapingVoidEntity(ModEntities.GAPING_VOID.get(), serverLevel);

        voidSphere.setPos(this.getX(), this.getY(), this.getZ());
        serverLevel.addFreshEntity(voidSphere);



        this.discard(); // Remove the projectile
    }

}
