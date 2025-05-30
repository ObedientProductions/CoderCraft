package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.custom.ModInfinityArmorItem;
import com.coderdan.avaritia.item.custom.SingularityItem;
import com.coderdan.avaritia.item.custom.UltimateItem;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {

        // Let /kill through
        if (event.getSource().getMsgId().equals("genericKill")) {
            return;
        }


        LivingEntity entity = event.getEntity();

        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);

        float reduction = 0f;

        if (head.getItem() == ModItems.INFINITY_HELMET.get()) {
            reduction += 0.20f;
        }
        if (chest.getItem() == ModItems.INFINITY_BREASTPLATE.get()) {
            reduction += 0.40f;
        }
        if (legs.getItem() == ModItems.INFINITY_LEGGINGS.get()) {
            reduction += 0.30f;
        }
        if (feet.getItem() == ModItems.INFINITY_BOOTS.get()) {
            reduction += 0.10f;
        }

        if (reduction >= 1.0f) {
            event.setCanceled(true); // full immunity
        } else if (reduction > 0f) {
            float reducedDamage = event.getAmount() * (1f - reduction);
            event.setAmount(reducedDamage);
        }

        System.out.println("Source: " + event.getSource());

    }


    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;

        boolean hasFullSet =
                player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.INFINITY_HELMET.get() &&
                        player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.INFINITY_BREASTPLATE.get() &&
                        player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.INFINITY_LEGGINGS.get() &&
                        player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.INFINITY_BOOTS.get();

        if (hasFullSet) {

            String[] messages = new String[]{
                    "§c§lDENIED.",
                    "§cA god refused death.",
                    "§cReality bends around %s's will.",
                    "§cDeath was §lnot permitted.",
                    "§c§lYour command has been ignored.",
                    "§cEven the void cannot claim %s.",
                    "§c§l%s laughs at mortality.",
                    "§cYour rules do not apply to %s.",
                    "§c§lNot today.",
                    "§c§lPower beyond understanding shields %s.",
                    "§cThe reaper turned away from %s.",
                    "§cYou dare challenge a god?",
                    "§c§l%s is eternal.",
                    "§cNot even /kill works on %s.",
                    "§c§l%s is beyond your reach.",
                    "§c%s shrugged off oblivion.",
                    "§c§lYou think a command could end %s?",
                    "§cThe world bends, but %s does not break.",
                    "§c§lImmortality has a name: %s.",
                    "§c%s stepped out of death's path.",
                    "§c§l%s's legend grows.",
                    "§cEven the system couldn’t stop %s.",
                    "§c§lFate looked away from %s.",
                    "§c%s simply refused to go.",
                    "§c§lInvincible. Untouchable. Unfazed.",
                    "§cYour attempt has been §ldenied."
            };

            String chosen = messages[(int)(Math.random() * messages.length)];
            String finalMessage = String.format(chosen, player.getName().getString());




            if (player.getServer() != null) {
                player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(finalMessage), false
                );
                event.setCanceled(true);
            }


            player.setHealth(player.getMaxHealth());
            player.level().broadcastEntityEvent(player, (byte) 35); // optional totem effect
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;

        Player player = event.player;

        boolean hasFullSet =
                player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.INFINITY_HELMET.get() &&
                        player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.INFINITY_BREASTPLATE.get() &&
                        player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.INFINITY_LEGGINGS.get() &&
                        player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.INFINITY_BOOTS.get();

        if (!player.level().isClientSide) {
            if (hasFullSet) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.getAbilities().setFlyingSpeed(0.2f);
                    player.onUpdateAbilities();
                }
            } else {
                if (player.getAbilities().mayfly && !player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.getAbilities().setFlyingSpeed(0.05f);
                    player.onUpdateAbilities();
                }
            }
        }
    }



    @SubscribeEvent
    public static void onPickup(EntityItemPickupEvent event) {
        ItemEntity entity = event.getItem();
        ItemStack incoming = entity.getItem();

        if (!incoming.is(ModItems.MATTER_CLUSTER.get())) return;

        Player player = event.getEntity();
        List<ItemStack> existingClusters = player.getInventory().items.stream()
                .filter(stack -> stack.is(ModItems.MATTER_CLUSTER.get()))
                .collect(Collectors.toList());

        // Try to merge into one with space
        for (ItemStack existing : existingClusters) {
            if (!existing.has(ModDataComponentTypes.STORED_LOOT.get())) continue;

            List<ItemStack> current = new ArrayList<>(existing.get(ModDataComponentTypes.STORED_LOOT.get()));
            int currentTotal = current.stream().mapToInt(ItemStack::getCount).sum();

            List<ItemStack> incomingLoot = incoming.get(ModDataComponentTypes.STORED_LOOT.get());
            int incomingTotal = incomingLoot.stream().mapToInt(ItemStack::getCount).sum();

            int spaceLeft = 4096 - currentTotal;
            if (spaceLeft <= 0) continue;

            List<ItemStack> toAdd = new ArrayList<>();
            List<ItemStack> leftover = new ArrayList<>();

            int added = 0;
            for (ItemStack s : incomingLoot) {
                int count = s.getCount();
                if (added + count <= spaceLeft) {
                    toAdd.add(s.copy());
                    added += count;
                } else {
                    int room = spaceLeft - added;
                    if (room > 0) {
                        ItemStack split = s.copy();
                        split.setCount(room);
                        toAdd.add(split);

                        ItemStack extra = s.copy();
                        extra.setCount(count - room);
                        leftover.add(extra);
                    } else {
                        leftover.add(s.copy());
                    }
                    break;
                }
            }

            if (!toAdd.isEmpty()) {
                current.addAll(toAdd);
                existing.set(ModDataComponentTypes.STORED_LOOT.get(), current);
                entity.setItem(createClusterWith(leftover));

                // Stop default pickup if we merged fully
                if (leftover.isEmpty()) {
                    entity.discard();
                    event.setCanceled(true);
                }

                return;
            }
        }
    }

    private static ItemStack createClusterWith(List<ItemStack> items) {
        if (items.isEmpty()) return ItemStack.EMPTY;
        ItemStack cluster = new ItemStack(ModItems.MATTER_CLUSTER.get());
        cluster.set(ModDataComponentTypes.STORED_LOOT.get(), items);
        return cluster;
    }



    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ModItems.SKULLFIRE_SWORD.get())) return;

        Entity target = event.getEntity();
        boolean isSkeleton = target instanceof Skeleton;
        boolean isWitherSkeleton = target instanceof WitherSkeleton;

        if (!isSkeleton && !isWitherSkeleton) return;

        // Remove any skulls already being dropped
        event.getDrops().removeIf(drop -> {
            ItemStack stack = drop.getItem();
            return stack.is(Items.SKELETON_SKULL) || stack.is(Items.WITHER_SKELETON_SKULL);
        });

        // Always add a wither skull instead
        event.getDrops().add(new ItemEntity(
                target.level(),
                target.getX(), target.getY(), target.getZ(),
                new ItemStack(Items.WITHER_SKELETON_SKULL)
        ));
    }







}
