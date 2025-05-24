package com.coderdan.avaritia.item;

import com.coderdan.avaritia.datagen.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class ModToolTiers {

    public static final Tier INFINITY = new ForgeTier(
            Integer.MAX_VALUE,
            100f, // speed
            10f, // attack bonus
            0, // enchant
            ModTags.Blocks.NEEDS_INFINITY_TOOL,
            () -> Ingredient.of(ModItems.INFINITY_INGOT.get()),
            ModTags.Blocks.INCORRECT_FOR_INFINITY_TOOL
    );
}
