package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.events.TooltipEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InfinityBootsItem extends ModInfinityArmorItem{
    public InfinityBootsItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }


    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.literal("§9+")
                .append(Component.literal(TooltipEventHandler.ColorTextCycler.styleSanicSequence("SANIC")))
                .append(Component.translatable("tooltip.avaritia.sanic_speed").withStyle(ChatFormatting.BLUE)));

    }
}
