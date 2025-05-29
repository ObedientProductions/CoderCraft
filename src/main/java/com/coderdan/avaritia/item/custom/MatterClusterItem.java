package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.item.ModDataComponentTypes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class MatterClusterItem extends Item {
    public MatterClusterItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide() && stack.has(ModDataComponentTypes.STORED_LOOT.get())) {
            List<ItemStack> drops = stack.get(ModDataComponentTypes.STORED_LOOT.get());

            for (ItemStack item : drops) {
                if (!pPlayer.addItem(item)) {
                    pPlayer.drop(item, false);
                }
            }

            pPlayer.displayClientMessage(Component.literal("§7Emptied Matter Cluster"), true);
            stack.shrink(1); // removes the item from hand
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§c" + translated);
    }



    public static int getTotalStoredItems(ItemStack cluster) {
        if (!cluster.has(ModDataComponentTypes.STORED_LOOT.get())) return 0;
        List<ItemStack> contents = cluster.get(ModDataComponentTypes.STORED_LOOT.get());
        return contents.stream().mapToInt(ItemStack::getCount).sum();
    }


    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        if (!pStack.has(ModDataComponentTypes.STORED_LOOT.get())) return;

        List<ItemStack> stored = pStack.get(ModDataComponentTypes.STORED_LOOT.get());
        int total = stored.stream().mapToInt(ItemStack::getCount).sum();

        pTooltipComponents.add(Component.literal("§7Stored: " + total + " / 4096 items"));

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.literal("§8Contents:"));
            stored.stream()
                    .collect(Collectors.groupingBy(ItemStack::getDisplayName, Collectors.summingInt(ItemStack::getCount)))
                    .forEach((name, count) -> pTooltipComponents.add(Component.literal(" " + count + "x ").append(name.copy().withStyle(style -> style.withColor(0xAAAAAA)))));
        } else {
            pTooltipComponents.add(Component.literal("§8<Hold Shift to view contents>"));
        }
    }
}
