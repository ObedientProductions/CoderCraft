package com.coderdan.avaritia.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DiamondLatticeItem extends TranslatableItem {
    public DiamondLatticeItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§e" + translated);
    }
}
