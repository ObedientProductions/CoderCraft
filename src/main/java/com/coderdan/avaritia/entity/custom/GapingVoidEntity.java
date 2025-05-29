package com.coderdan.avaritia.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GapingVoidEntity extends Entity {

    public int age = 0;
    public static final int MAX_AGE = 100;
    public static final float RADIUS = 6f;

    public GapingVoidEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        age = pCompound.getInt("age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("age", age);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        age++;
        if (age >= MAX_AGE) {
            level().explode(this, getX(), getY(), getZ(), 4.0f, Level.ExplosionInteraction.MOB);
            discard();
            return;
        }

        Vec3 center = position();
        AABB effectArea = getBoundingBox().inflate(RADIUS);

        // Pull and damage entities
        List<Entity> nearby = level().getEntities(this, effectArea, e -> e != this && !(e instanceof Player player && player.isCreative()));

        for (Entity entity : nearby) {
            Vec3 pull = center.subtract(entity.position()).normalize().scale(0.2);
            entity.setDeltaMovement(entity.getDeltaMovement().add(pull));
            if (entity instanceof LivingEntity le) {
                le.hurt(damageSources().magic(), 1f);
            }
        }


        // Break blocks every 10 ticks
        if (age % 10 == 0) {
            float currentRadius = Mth.clamp((float) age / MAX_AGE, 0f, 1f) * RADIUS;
            BlockPos centerPos = blockPosition();

            BlockPos.betweenClosed(
                    centerPos.offset((int) -RADIUS, (int) -RADIUS, (int) -RADIUS),
                    centerPos.offset((int) RADIUS, (int) RADIUS, (int) RADIUS)
            ).forEach(pos -> {
                double dx = pos.getX() + 0.5 - getX();
                double dy = pos.getY() + 0.5 - getY();
                double dz = pos.getZ() + 0.5 - getZ();
                double distanceSquared = dx * dx + dy * dy + dz * dz;

                if (distanceSquared <= currentRadius * currentRadius) {

                    BlockState state = level().getBlockState(pos);
                    float hardness = state.getDestroySpeed(level(), pos);

                    if (!state.isAir() && hardness >= 0f && hardness < 10f){
                        level().destroyBlock(pos, false);
                    }
                }
            });
        }


    }

    public float getProgress() {
        return (float) age / MAX_AGE;
    }



    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }
}
