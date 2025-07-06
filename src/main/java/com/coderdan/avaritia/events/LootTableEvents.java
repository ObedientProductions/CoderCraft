package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModConfig;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableEvents {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().toString().equals(BuiltInLootTables.END_CITY_TREASURE.location().toString())) {


            float chance = 1.0f / Math.max(ModConfig.TRIM_SPAWN_CHANCE, 1);


            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get())
                            .when(LootItemRandomChanceCondition.randomChance(chance)))
                    .setRolls(ConstantValue.exactly(1))
                    .build();

            event.getTable().addPool(pool);
        }
    }
}

