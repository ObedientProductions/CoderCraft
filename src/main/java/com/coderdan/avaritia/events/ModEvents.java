package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFFFD700; // bright gold
                if (tintIndex == 0) return 0xFFCCAA00; // darker gold
                return 0xFFFFFFFF;
            }
        }, ModItems.GOLD_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFE5E5E5; // light soft silver
                if (tintIndex == 0) return 0xFFAAAAAA; // darker soft gray
                return 0xFFFFFFFF;
            }
        }, ModItems.IRON_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF2D5EFF; // bright lapis
                if (tintIndex == 0) return 0xFF1B3A99; // dark lapis
                return 0xFFFFFFFF;
            }
        }, ModItems.LAPIS_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFFF1A1A; // bright redstone
                if (tintIndex == 0) return 0xFF991111; // dark redstone
                return 0xFFFFFFFF;
            }
        }, ModItems.REDSTONE_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFFFE0CC; // quartz bright
                if (tintIndex == 0) return 0xFFCCBBAA; // quartz darker
                return 0xFFFFFFFF;
            }
        }, ModItems.NETHER_QUARTZ_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFFFA14D; // bright copper
                if (tintIndex == 0) return 0xFF995D2A; // dark copper
                return 0xFFFFFFFF;
            }
        }, ModItems.COPPER_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFC4F0FF; // light tin
                if (tintIndex == 0) return 0xFF94B3CC; // dark tin
                return 0xFFFFFFFF;
            }
        }, ModItems.TIN_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF9A84B3; // soft purple-blue (bright side)
                if (tintIndex == 0) return 0xFF5A4A70; // dark muted purple (shadow side)
                return 0xFFFFFFFF;
            }
        }, ModItems.LEAD_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF999999; // bright silver
                if (tintIndex == 0) return 0xFF666666; // dark silver
                return 0xFFFFFFFF;
            }
        }, ModItems.SILVER_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFC8B684; // nickel bright
                if (tintIndex == 0) return 0xFF8C7A4C; // nickel dark
                return 0xFFFFFFFF;
            }
        }, ModItems.NICKEL_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFD8FAFF; // super soft crystal white-blue
                if (tintIndex == 0) return 0xFFAEEFF5; // very bright, almost white-blue
                return 0xFFFFFFFF;
            }
        }, ModItems.DIAMOND_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF00FF55; // emerald bright
                if (tintIndex == 0) return 0xFF007A2A; // emerald dark
                return 0xFFFFFFFF;
            }
        }, ModItems.EMERALD_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF404040; // netherite bright (dark gray)
                if (tintIndex == 0) return 0xFF202020; // netherite darker
                return 0xFFFFFFFF;
            }
        }, ModItems.NETHERITE_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFCC3333; // slightly brighter dark red
                if (tintIndex == 0) return 0xFFE6D28A; // brighter warm beige/yellow
                return 0xFFFFFFFF;
            }
        }, ModItems.FLUXED_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFF76D7EA; // platinum bright (pale blue)
                if (tintIndex == 0) return 0xFF41748D; // platinum dark blue
                return 0xFFFFFFFF;
            }
        }, ModItems.PLATINUM_SINGULARITY.get());

        event.getItemColors().register(new ItemColor() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                if (tintIndex == 1) return 0xFFC8F4FF; // iridium bright
                if (tintIndex == 0) return 0xFF8AA6B3; // iridium dark
                return 0xFFFFFFFF;
            }
        }, ModItems.IRIDIUM_SINGULARITY.get());
    }

}
