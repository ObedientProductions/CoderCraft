package com.coderdan.avaritia.entity.custom;

import com.coderdan.avaritia.entity.ModEntities;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class InfinityArrowEntity extends AbstractArrow {

    private final Item referenceItem;

    public InfinityArrowEntity(EntityType<? extends InfinityArrowEntity> type, Level level) {
        super(type, level);
        this.referenceItem = ModItems.INFINITY_ARROW.get();
    }


    public InfinityArrowEntity(Level level, LivingEntity shooter, Item referenceItem) {
        super(ModEntities.INFINITY_ARROW.get(), level); // Only this constructor is available now
        this.referenceItem = referenceItem;

        if (shooter != null) {
            this.setOwner(shooter);
            this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        } else {
            this.setPos(this.getX(), this.getY(), this.getZ()); // fallback
        }


    }


    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.INFINITY_ARROW.get());
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    int ticksElapsed = 0;
    int randomDiscardTime = -1;
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && !this.inGround) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                        ParticleTypes.END_ROD, // or something else like ENCHANT, FLAME, etc
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0
                );

                this.level().addParticle(
                        ParticleTypes.FIREWORK, // or something else like ENCHANT, FLAME, etc
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0
                );
            }
        }


        // Auto-discard after random time in ground
        if (!this.level().isClientSide) {
            if (this.inGround) {
                ticksElapsed++;

                if (randomDiscardTime == -1) {
                    // 3 to 8 seconds (in ticks)
                    randomDiscardTime = 60 + this.random.nextInt(5 * 20); // 60 to 160 ticks
                }

                if (ticksElapsed >= randomDiscardTime) {
                    this.discard();
                }
            } else {
                ticksElapsed = 0;
                randomDiscardTime = -1;
            }
        }
    }

    private boolean spawnedByArrowRain = false;

    public void setSpawnedByArrowRain(boolean value) {
        this.spawnedByArrowRain = value;
    }


    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide && !spawnedByArrowRain) {
            for (int i = 0; i < 30; i++) {
                InfinityArrowEntity extraArrow = new InfinityArrowEntity(this.level(), (LivingEntity) this.getOwner(), ModItems.INFINITY_ARROW.get());

                extraArrow.setPos(this.getX() + (this.random.nextDouble() - 0.5) * 4.0,
                        this.getY() + 10.0,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 4.0);
                extraArrow.shoot(0, -1, 0, 2.5f, 0f);
                extraArrow.setSpawnedByArrowRain(true); // prevent infinite rain

                this.level().addFreshEntity(extraArrow);
            }
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level().isClientSide) {
            Entity hit = pResult.getEntity();

            // Special handling for Ender Dragon
            if (hit instanceof EnderDragonPart part && part.parentMob instanceof EnderDragon dragon) {
                dragon.invulnerableTime = 0;

                float currentHealth = dragon.getHealth();
                boolean killed = dragon.hurt(dragon.damageSources().arrow(this, this.getOwner()), currentHealth);

                if (killed || dragon.getHealth() <= 0.0F) {
                    dragon.getPhaseManager().setPhase(EnderDragonPhase.DYING);
                }

            }
            // Normal entity instant kill logic
            else if (hit instanceof LivingEntity target) {
                target.invulnerableTime = 0;


                target.hurt(target.damageSources().arrow(this, this.getOwner()), Integer.MAX_VALUE);

                if (target.isAlive()) {
                    target.kill(); // Just in case hurt didn't kill them (e.g., withers or weird bosses)
                }
            }

            this.discard(); // Always discard arrow after hit
        }
    }


}
