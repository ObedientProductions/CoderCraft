package com.coderdan.avaritia.item.custom;

import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class SkullFireSwordItem extends SwordItem {

    public SkullFireSwordItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }


}


