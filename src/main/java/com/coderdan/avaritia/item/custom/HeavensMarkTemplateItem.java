package com.coderdan.avaritia.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class HeavensMarkTemplateItem extends SmithingTemplateItem {
    public HeavensMarkTemplateItem(Component pAppliesTo, Component pIngredients, Component pUpgradeDescription, Component pBaseSlotDescription, Component pAdditionsSlotDescription, List<ResourceLocation> pBaseSlotEmptyIcons, List<ResourceLocation> pAdditionalSlotEmptyIcons, FeatureFlag... pRequiredFeatures) {
        super(pAppliesTo, pIngredients, pUpgradeDescription, pBaseSlotDescription, pAdditionsSlotDescription, pBaseSlotEmptyIcons, pAdditionalSlotEmptyIcons, pRequiredFeatures);
    }


    @Override
    public Component getName(ItemStack pStack) {

        String translated = Component.translatable(this.getDescriptionId()).getString();
        return Component.literal("§6" + translated);
    }



}
