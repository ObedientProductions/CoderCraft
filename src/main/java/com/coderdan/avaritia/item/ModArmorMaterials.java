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

    public static final Holder<ArmorMaterial> INFINITY_MATERIAL = register("infinity", Util.make(
            new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, Integer.MAX_VALUE);
                attribute.put(ArmorItem.Type.LEGGINGS, Integer.MAX_VALUE);
                attribute.put(ArmorItem.Type.CHESTPLATE, Integer.MAX_VALUE);
                attribute.put(ArmorItem.Type.HELMET, Integer.MAX_VALUE);
            }), Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, () -> ModItems.INFINITY_INGOT.get());



    public static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> typeProtection,
                                                 int enchantablity, float toughness, float knockbackResistance,
                                                 Supplier<Item> ingredientItem){

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, name);

        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());

        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));

        EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type type : ArmorItem.Type.values()){
            typeMap.put(type, typeProtection.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location, new ArmorMaterial(typeProtection, enchantablity, equipSound, ingredient, layers, toughness, knockbackResistance));
    }
}
