package com.coderdan.avaritia.item.creativetabs;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.ModBlocks;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Avaritia.MOD_ID);

    public static final RegistryObject<CreativeModeTab> avaritia_TAB = CREATIVE_MODE_TABS.register("avaritia_tab",
            () -> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(ModItems.INFINITY_CATALYST.get()))
                    .title(Component.translatable("creativetab.avaritia.avaritia_tab"))
                    .displayItems((itemDisplayParameters, output) ->
                        {
                            //output.accept(ModItems.TITANIUM.get());
                            //output.accept(ModItems.TITANIUM_INGOT.get());
                            //output.accept(ModBlocks.TITANIUM_BLOCK.get());
                            //output.accept(ModBlocks.RAW_TITANIUM_BLOCK.get());

                            output.accept(ModItems.DIAMOND_LATTICE.get());
                            output.accept(ModItems.CRYSTAL_MATRIX_INGOT.get());
                            output.accept(ModItems.PILE_OF_NEUTRONS.get());
                            output.accept(ModItems.NEUTRONIUM_NUGGET.get());
                            output.accept(ModItems.NEUTRONIUM_INGOT.get());

                            output.accept(ModItems.INFINITY_CATALYST.get());
                            output.accept(ModItems.INFINITY_INGOT.get());
                            output.accept(ModItems.RECORD_FRAGMENT.get());
                            output.accept(ModItems.DNB_MUSIC_DISC.get());

                            output.accept(ModItems.IRON_SINGULARITY.get());
                            output.accept(ModItems.GOLD_SINGULARITY.get());
                            output.accept(ModItems.LAPIS_SINGULARITY.get());
                            output.accept(ModItems.REDSTONE_SINGULARITY.get());
                            output.accept(ModItems.NETHER_QUARTZ_SINGULARITY.get());
                            output.accept(ModItems.COPPER_SINGULARITY.get());
                            output.accept(ModItems.TIN_SINGULARITY.get());
                            output.accept(ModItems.LEAD_SINGULARITY.get());
                            output.accept(ModItems.SILVER_SINGULARITY.get());
                            output.accept(ModItems.NICKEL_SINGULARITY.get());
                            output.accept(ModItems.DIAMOND_SINGULARITY.get());
                            output.accept(ModItems.EMERALD_SINGULARITY.get());
                            output.accept(ModItems.NETHERITE_SINGULARITY.get());
                            output.accept(ModItems.FLUXED_SINGULARITY.get());
                            output.accept(ModItems.PLATINUM_SINGULARITY.get());
                            output.accept(ModItems.IRIDIUM_SINGULARITY.get());

                            output.accept(ModItems.INFINITY_SWORD.get());
                            output.accept(ModItems.INFINITY_BOW.get());

                            ItemStack stack = new ItemStack(ModItems.INFINITY_PICKAXE.get());
                            Holder<Enchantment> fortune = Enchantments.FORTUNE.getOrThrow(Minecraft.getInstance().level);
                            stack.enchant(fortune, 10);
                            output.accept(stack);


                            output.accept(ModItems.INFINITY_SHOVEL.get());
                            output.accept(ModItems.INFINITY_AXE.get());
                            output.accept(ModItems.INFINITY_HOE.get());

                            output.accept(ModItems.INFINITY_HELMET.get());
                            output.accept(ModItems.INFINITY_BREASTPLATE.get());
                            output.accept(ModItems.INFINITY_LEGGINGS.get());
                            output.accept(ModItems.INFINITY_BOOTS.get());

                            output.accept(ModItems.SKULLFIRE_SWORD.get());





                            output.accept(ModItems.ENDEST_PEARL.get());
                            output.accept(ModItems.SPECIAL_ITEM.get());
                            output.accept(ModItems.ULTIMATE_STEW.get());
                            output.accept(ModItems.COSMIC_MEATBALLS.get());



                            //output.accept(ModBlocks.TRANSMUTER_BLOCK.get());
                            //output.accept(ModBlocks.CRAFTER.get());
                            //output.accept(ModBlocks.CRAFTER.get());


                            output.accept(ModBlocks.COMPRESSED_CRAFTING_TABLE.get());
                            output.accept(ModBlocks.DOUBLE_COMPRESSED_CRAFTING_TABLE.get());
                            output.accept(ModBlocks.EXTREME_CRAFTING_TABLE.get());
                            output.accept(ModBlocks.NEUTRONIUM_BLOCK.get());
                            output.accept(ModBlocks.INFINITY_BLOCK.get());
                            output.accept(ModBlocks.CRYSTAL_MATRIX.get());
                            output.accept(ModBlocks.COLLECTOR.get());
                            output.accept(ModBlocks.COMPRESSOR.get());

                            output.accept(ModItems.HEAVENS_MARK_SMITHING_TEMPLATE.get());
                        })

                    .build());

    public static void register(IEventBus bus)
    {
        CREATIVE_MODE_TABS.register(bus);
    }
}
