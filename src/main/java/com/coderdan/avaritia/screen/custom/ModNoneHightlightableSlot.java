package com.coderdan.avaritia.screen.custom;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModNoneHightlightableSlot extends Slot {
    public ModNoneHightlightableSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean isHighlightable() {
        return false;
    }

    @Override
    public boolean allowModification(Player pPlayer) {
        return false;
    }


}
