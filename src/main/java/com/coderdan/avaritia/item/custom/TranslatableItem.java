package com.coderdan.avaritia.item.custom;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TranslatableItem extends Item {
    public TranslatableItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        String key = pStack.getItem().getDescriptionId() + ".desc";

        if (I18n.exists(key)) {
            String desc = I18n.get(key); // gets raw translated string
            pTooltipComponents.add(Component.literal("§8" + desc));
        }
    }
}
