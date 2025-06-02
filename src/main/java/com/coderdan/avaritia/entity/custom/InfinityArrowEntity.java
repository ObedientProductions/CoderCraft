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

    private boolean spawnedByArrowRain = false;
    private int arrowRainTimer = 0;
    private int spawnedArrows = 0;
    private final int totalRainArrows = 10;
    private boolean shouldRain = false;



    @Override
    public void tick() {
        super.tick();


        if (!this.level().isClientSide
                && shouldRain
                && inGround
                && spawnedArrows < totalRainArrows
                && this.level().canSeeSky(this.blockPosition())){

            arrowRainTimer++;

            if (arrowRainTimer % 2 == 0) {
                InfinityArrowEntity extraArrow = new InfinityArrowEntity(this.level(), (LivingEntity) this.getOwner(), ModItems.INFINITY_ARROW.get());

                extraArrow.setPos(this.getX(), this.getY() + 10.0, this.getZ());

                double dx = (this.random.nextDouble() - 0.5) * 0.5;
                double dz = (this.random.nextDouble() - 0.5) * 0.5;

                extraArrow.shoot(dx, -1.0, dz, 2.5f, 5f);
                extraArrow.setSpawnedByArrowRain(true); // child arrows never rain

                this.level().addFreshEntity(extraArrow);
                spawnedArrows++;
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



    public void setSpawnedByArrowRain(boolean value) {
        this.spawnedByArrowRain = value;
    }


    @Override
    protected void onHit(HitResult result) {

        if (!spawnedByArrowRain) {
            this.shouldRain = true;
            if (!this.level().isClientSide) {
                this.arrowRainTimer = 0;
                this.spawnedArrows = 0;
            }
        }



        super.onHit(result);

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
