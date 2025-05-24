package com.coderdan.avaritia.item;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.openjdk.nashorn.internal.codegen.TypeMap;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

    int durabilityMultiplier = 9999;



    public static final Holder<ArmorMaterial> INFINITY_MATERIAL = register("infinity", Util.make(
                    new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), attribute -> {
                        attribute.put(ArmorItem.Type.BOOTS, 9999);
                        attribute.put(ArmorItem.Type.LEGGINGS, 9999);
                        attribute.put(ArmorItem.Type.CHESTPLATE, 9999);
                        attribute.put(ArmorItem.Type.HELMET, 9999);
                    }),
            0, // enchantability
            999.0f, // toughness
            1.0f, // knockbackResistance
            () -> ModItems.INFINITY_INGOT.get()
    );





    public static Holder<ArmorMaterial> register(String name,
                                                 EnumMap<ArmorItem.Type, Integer> typeProtection,
                                                 int enchantability,
                                                 float toughness,
                                                 float knockbackResistance,
                                                 Supplier<Item> ingredientItem) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, name);

        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());

        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location,
                new ArmorMaterial(typeProtection, enchantability, equipSound, ingredient, layers, toughness, knockbackResistance));
    }


}
