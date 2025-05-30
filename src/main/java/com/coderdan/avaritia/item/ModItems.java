package com.coderdan.avaritia.item;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.custom.*;
import com.coderdan.avaritia.item.render.InfinitySwordRenderer;
import com.coderdan.avaritia.sound.ModSounds;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Avaritia.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LATTICE = ITEMS.register("diamond_lattice",
            () -> new DiamondLatticeItem(new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_MATRIX_INGOT = ITEMS.register("crystal_matrix_ingot",
            () -> new CrystalMatrixIngotItem(new Item.Properties()));

    public static final RegistryObject<Item> PILE_OF_NEUTRONS = ITEMS.register("pile_of_neutrons",
            () -> new NeutronPileItem(new Item.Properties()));

    public static final RegistryObject<Item> NEUTRONIUM_NUGGET = ITEMS.register("neutronium_nugget",
            () -> new NeutronNuggetItem(new Item.Properties()));

    public static final RegistryObject<Item> NEUTRONIUM_INGOT = ITEMS.register("neutronium_ingot",
            () -> new NeutronIngotItem(new Item.Properties()));

    public static final RegistryObject<Item> INFINITY_CATALYST = ITEMS.register("infinity_catalyst",
            () -> new InfinityCatalystItem(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> INFINITY_INGOT = ITEMS.register("infinity_ingot",
            () -> new InfinityIngotItem(new Item.Properties().fireResistant()));


    public static final RegistryObject<Item> RECORD_FRAGMENT = ITEMS.register("record_fragment",
            () -> new RecordFragmentItem(new Item.Properties()));



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
            () -> new EndestPearlItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> ULTIMATE_STEW = ITEMS.register("ultimate_stew",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COSMIC_MEATBALLS = ITEMS.register("cosmic_meatballs",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("special_item",
            () -> new Specialitem(new Item.Properties().durability(32)));


    public static final RegistryObject<Item> INFINITY_HELMET = ITEMS.register("infinity_helmet",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(Integer.MAX_VALUE).fireResistant()));

    public static final RegistryObject<Item> INFINITY_BREASTPLATE = ITEMS.register("infinity_breastplate",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(Integer.MAX_VALUE)));

    public static final RegistryObject<Item> INFINITY_LEGGINGS = ITEMS.register("infinity_leggings",
            () -> new ModInfinityArmorItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(Integer.MAX_VALUE).fireResistant()));

    public static final RegistryObject<Item> INFINITY_BOOTS = ITEMS.register("infinity_boots",
            () -> new InfinityBootsItem(ModArmorMaterials.INFINITY_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(Integer.MAX_VALUE).fireResistant()));



    public static final RegistryObject<Item> INFINITY_SWORD = ITEMS.register("infinity_sword",
            () -> new InfinitySwordItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(InfinitySwordItem.createAttributes(ModToolTiers.INFINITY, 3,-2.4f))));

    public static final RegistryObject<Item> SKULLFIRE_SWORD = ITEMS.register("skullfire_sword",
            () -> new SkullFireSwordItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(1000)
                    .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 7,-2.4f))));




    public static final RegistryObject<Item> INFINITY_PICKAXE = ITEMS.register("infinity_pickaxe",
            () -> new InfinityPickaxeItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.INFINITY, 1,-2.8f))));

    public static final RegistryObject<Item> INFINITY_SHOVEL = ITEMS.register("infinity_shovel",
            () -> new InfinityShovelItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.INFINITY, 1.5f,-3.0f))));

    public static final RegistryObject<Item> INFINITY_AXE = ITEMS.register("infinity_axe",
            () -> new InfinityAxeItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(AxeItem.createAttributes(ModToolTiers.INFINITY, 6,-3.2f))));

    public static final RegistryObject<Item> INFINITY_HOE = ITEMS.register("infinity_hoe",
            () -> new InfinityHoeItem(ModToolTiers.INFINITY, new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(HoeItem.createAttributes(ModToolTiers.INFINITY, 0,-3.0f))));

    public static final RegistryObject<Item> INFINITY_BOW = ITEMS.register("infinity_bow",
            () -> new InfinityBowItem(new Item.Properties()
                    .durability(Integer.MAX_VALUE)
                    .fireResistant()
                    .attributes(HoeItem.createAttributes(ModToolTiers.INFINITY, 0,-3.0f))));

    public static final RegistryObject<Item> MATTER_CLUSTER = ITEMS.register("matter_cluster",
            () -> new MatterClusterItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFINITY_ARROW = ITEMS.register("infinity_arrow", () ->
            new InfinityArrowItem(new Item.Properties(),  Float.MAX_VALUE));

    public static final RegistryObject<Item> HEAVENS_MARK_SMITHING_TEMPLATE = ITEMS.register("heavensmark_armor_trim_smithing_template",
            () -> HeavensMarkTemplateItem.createArmorTrimTemplate(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "heavens_mark")));

    public static final RegistryObject<Item> DNB_MUSIC_DISC = ITEMS.register("dnb_musicdisc",
            () -> new DnbMusicDiscItem(new Item.Properties().stacksTo(1).jukeboxPlayable(ModSounds.DNB_BEATBOX_KEY)));






    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
