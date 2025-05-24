package com.coderdan.avaritia.item.utils;

import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ModItemUtils {
    public static void dropMatterCluster(Level level, Player player, List<ItemStack> collected) {
        if (collected.isEmpty()) return;

        ItemStack cluster = new ItemStack(ModItems.MATTER_CLUSTER.get());
        cluster.set(ModDataComponentTypes.STORED_LOOT.get(), collected);

        ItemEntity clusterDrop = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), cluster);
        level.addFreshEntity(clusterDrop);
    }
}
