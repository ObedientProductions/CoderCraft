package com.coderdan.avaritia.item;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ModItemProperties {

    public static void addCustomItemProperties()
    {
        ItemProperties.register(ModItems.INFINITY_PICKAXE.get(), ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "used"),
                (itemStack, clientLevel, livingEntity, i) -> itemStack.get(ModDataComponentTypes.IS_WORLD_BREAKING.get()) != null ? 1f : 0f);

        ItemProperties.register(ModItems.INFINITY_SHOVEL.get(), ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "used"),
                (itemStack, clientLevel, livingEntity, i) -> itemStack.get(ModDataComponentTypes.IS_WORLD_BREAKING.get()) != null ? 1f : 0f);

        ItemProperties.register(ModItems.MATTER_CLUSTER.get(), ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "full"),
                (stack, level, entity, seed) -> {
                    if (!stack.has(ModDataComponentTypes.STORED_LOOT.get())) return 0f;
                    List<ItemStack> contents = stack.get(ModDataComponentTypes.STORED_LOOT.get());
                    int total = contents.stream().mapToInt(ItemStack::getCount).sum();
                    return total >= 4096 ? 1f : 0f;
                });

        makeCustomBowProperties(ModItems.INFINITY_BOW.get());
    }

    private static void makeCustomBowProperties(Item item)
    {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, p_340954_) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 7.0F;
            }
        });
        ItemProperties.register(
                item,
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, cLevel, livingEntity, p_174633_) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F
        );
    }
}



