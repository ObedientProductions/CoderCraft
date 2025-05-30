package com.coderdan.avaritia.item.projectiles;

import com.coderdan.avaritia.entity.ModEntities;
import com.coderdan.avaritia.entity.custom.GapingVoidEntity;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashSet;
import java.util.Set;

public class ThrownSpecialItemProjectile extends ThrowableItemProjectile {
    public ThrownSpecialItemProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownSpecialItemProjectile(Level pLevel, LivingEntity pShooter) {
        super(EntityType.EGG, pShooter, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SPECIAL_ITEM.get();
    }

    private final Set<BlockPos> placedLights = new HashSet<>();

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            BlockPos posBelow = this.blockPosition();

            if (level().getBlockState(posBelow).isAir()) {
                level().setBlockAndUpdate(posBelow, Blocks.LIGHT.defaultBlockState().setValue(BlockStateProperties.LEVEL, 15));
                placedLights.add(posBelow.immutable()); // Store copy for cleanup
            }
        }
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
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }

        if (!(level() instanceof ServerLevel serverLevel)) return;


        BlockPos center = this.blockPosition();

        int range = 10;
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos targetPos = center.offset(dx, dy, dz);
                    var blockState = serverLevel.getBlockState(targetPos);

                    if (!blockState.isAir() && blockState.getDestroySpeed(serverLevel, targetPos) <= 10){
                        serverLevel.setBlockAndUpdate(targetPos, Blocks.GOLD_BLOCK.defaultBlockState());
                    }


                }
            }
        }

        serverLevel.explode(
                this.getOwner(),                   // source entity (null = no source)
                getX(), getY(), getZ(), // explosion position
                20.0f,                   // explosion power (adjust as needed)
                Level.ExplosionInteraction.BLOCK
        );



        // Remove all previously placed light blocks
        removeLights();

        this.discard(); // Remove the projectile
    }


    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        // Remove all previously placed light blocks
        removeLights();
        super.onHitBlock(pResult);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {

        // Remove all previously placed light blocks
        removeLights();

        return super.hurt(pSource, pAmount);
    }


    void removeLights()
    {
        // Remove all previously placed light blocks
        for (BlockPos pos : placedLights) {
            if (level().getBlockState(pos).is(Blocks.LIGHT)) {
                level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        placedLights.clear();

        this.discard(); // Remove the projectile
    }
}
