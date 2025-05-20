package com.coderdan.avaritia.item;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.custom.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Avaritia.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LATTICE = ITEMS.register("diamond_lattice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_MATRIX_INGOT = ITEMS.register("crystal_matrix_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PILE_OF_NEUTRONS = ITEMS.register("pile_of_neutrons",
            () -> new NeutronPileItem(new Item.Properties()));

    public static final RegistryObject<Item> NEUTRONIUM_NUGGET = ITEMS.register("neutronium_nugget",
            () -> new NeutronNuggetItem(new Item.Properties()));

    public static final RegistryObject<Item> NEUTRONIUM_INGOT = ITEMS.register("neutronium_ingot",
            () -> new NeutronIngotItem(new Item.Properties()));

    public static final RegistryObject<Item> INFINITY_CATALYST = ITEMS.register("infinity_catalyst",
            () -> new UltimateItem(new Item.Properties()));

    public static final RegistryObject<Item> INFINITY_INGOT = ITEMS.register("infinity_ingot",
            () -> new UltimateItem(new Item.Properties()));


    public static final RegistryObject<Item> RECORD_FRAGMENT = ITEMS.register("record_fragment",
            () -> new Item(new Item.Properties()));



    public static final RegistryObject<Item> IRON_SINGULARITY = ITEMS.register("iron_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> GOLD_SINGULARITY = ITEMS.register("gold_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> LAPIS_SINGULARITY = ITEMS.register("lapis_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> REDSTONE_SINGULARITY = ITEMS.register("redstone_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> NETHER_QUARTZ_SINGULARITY = ITEMS.register("nether_quartz_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> COPPER_SINGULARITY = ITEMS.register("copper_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> TIN_SINGULARITY = ITEMS.register("tin_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> LEAD_SINGULARITY = ITEMS.register("lead_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> SILVER_SINGULARITY = ITEMS.register("silver_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> NICKEL_SINGULARITY = ITEMS.register("nickel_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> DIAMOND_SINGULARITY = ITEMS.register("diamond_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> EMERALD_SINGULARITY = ITEMS.register("emerald_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_SINGULARITY = ITEMS.register("netherite_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> FLUXED_SINGULARITY = ITEMS.register("fluxed_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> PLATINUM_SINGULARITY = ITEMS.register("platinum_singularity",
            () -> new SingularityItem(new Item.Properties()));

    public static final RegistryObject<Item> IRIDIUM_SINGULARITY = ITEMS.register("iridium_singularity",
            () -> new SingularityItem(new Item.Properties()));



    public static final RegistryObject<Item> ENDEST_PEARL = ITEMS.register("endest_pearl",
            () -> new EndestPearlItem(new Item.Properties()));

    public static final RegistryObject<Item> ULTIMATE_STEW = ITEMS.register("ultimate_stew",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COSMIC_MEATBALLS = ITEMS.register("cosmic_meatballs",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("special_item",
            () -> new Specialitem(new Item.Properties().durability(32)));

    public static final RegistryObject<Item> INFINITY_HELMET = ITEMS.register("infinity_helmet",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(Integer.MAX_VALUE))));

    public static final RegistryObject<Item> INFINITY_BREASTPLATE = ITEMS.register("infinity_breastplate",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(Integer.MAX_VALUE))));

    public static final RegistryObject<Item> INFINITY_LEGGINGS = ITEMS.register("infinity_leggings",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(Integer.MAX_VALUE))));

    public static final RegistryObject<Item> INFINITY_BOOTS = ITEMS.register("infinity_boots",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(Integer.MAX_VALUE))));



    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
