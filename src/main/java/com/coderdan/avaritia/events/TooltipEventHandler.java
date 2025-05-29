package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.ModItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, value = Dist.CLIENT)
public class TooltipEventHandler {

    //Recolor stats
    @SubscribeEvent
    public static void onTooltipGather(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        boolean removedStats = false;

        if (Objects.equals(stack.getItem().getCreatorModId(stack), Avaritia.MOD_ID)) {
            List<Either<FormattedText, TooltipComponent>> tooltips = event.getTooltipElements();

            String damage = null;
            String speed = null;

            Iterator<Either<FormattedText, TooltipComponent>> iter = tooltips.iterator();
            while (iter.hasNext()) {
                Either<FormattedText, TooltipComponent> line = iter.next();
                if (line.left().isPresent()) {
                    String raw = line.left().get().getString().toLowerCase(Locale.ROOT);

                    if (raw.contains(Component.translatable("attribute.name.generic.attack_damage").getString().toLowerCase())) {
                        damage = line.left().get().getString().replaceAll("[^0-9.+-]", "");
                        iter.remove();
                        removedStats = true;
                    } else if (raw.contains(Component.translatable("attribute.name.generic.attack_speed").getString().toLowerCase())) {
                        speed = line.left().get().getString().replaceAll("[^0-9.+-]", "");
                        iter.remove();
                        removedStats = true;
                    } else if (raw.contains(Component.translatable("item.modifiers.mainhand").getString().toLowerCase())) {
                        iter.remove();
                        removedStats = true;
                    }
                }
            }

            if (removedStats) {
                tooltips.add(Either.left(Component.translatable("item.modifiers.mainhand").withStyle(ChatFormatting.DARK_GRAY)));

                if (stack.is(ModItems.INFINITY_SWORD.get())) {
                    if (speed == null) return;
                    tooltips.add(Either.left(Component.literal("§9+" + speed + " ")
                            .append(Component.translatable("attribute.name.generic.attack_speed").withStyle(ChatFormatting.BLUE))));
                    tooltips.add(Either.left(Component.literal("§9+" + ColorTextCycler.styleRainbow(
                                    Component.translatable("tooltip.avaritia.infinity_damage").getString()))
                            .append(" ")
                            .append(Component.translatable("attribute.name.generic.attack_damage").withStyle(ChatFormatting.BLUE))));
                } else if (stack.is(ModItems.INFINITY_BOW.get())) {
                    tooltips.add(Either.left(Component.literal("§9+" + ColorTextCycler.styleRainbow(
                                    Component.translatable("tooltip.avaritia.infinity_damage").getString()))
                            .append(" ")
                            .append(Component.translatable("tooltip.avaritia.projectile_damage").withStyle(ChatFormatting.BLUE))));
                } else {
                    if (damage != null) {
                        tooltips.add(Either.left(Component.literal("§9+" + damage + " ")
                                .append(Component.translatable("attribute.name.generic.attack_damage").withStyle(ChatFormatting.BLUE))));
                    }
                    if (speed != null) {
                        tooltips.add(Either.left(Component.literal("§9+" + speed + " ")
                                .append(Component.translatable("attribute.name.generic.attack_speed").withStyle(ChatFormatting.BLUE))));
                    }
                }
            }
        }
    }


    public class ColorTextCycler {
        private static final String[] COLORS = {"§c", "§6", "§e", "§a", "§b", "§9", "§d"};
        private static final int COLOR_COUNT = COLORS.length;
        private static int tick = 0;

        public static String styleRainbow(String text) {
            StringBuilder sb = new StringBuilder();
            int shift = tick % COLOR_COUNT;

            for (int i = 0; i < text.length(); i++) {
                // Rotate colors rightward: character i gets the color that was at (i - tick)
                int colorIndex = (i - shift + COLOR_COUNT) % COLOR_COUNT;
                sb.append(COLORS[colorIndex]).append(text.charAt(i));
            }

            return sb.toString();
        }

        int startTick = 0;
        // Color codes in sequence
        private static final String[] SANIC_COLOR_SEQUENCE = {
                "§f", "§c", "§9", "§f", "§f", "§9", "§f", "§f", "§9","§f", "§9", "§9", "§9", "§9",
                "§7", "§7", "§7", "§7", "§7", "§7", "§7", "§7", "§7", "§7","§7", "§7","§7", "§7", "§7", "§7","§7", "§7",

        };

        public static String styleSanicSequence(String text) {
            StringBuilder sb = new StringBuilder();
            int sequenceTick = tick % SANIC_COLOR_SEQUENCE.length;
            int len = text.length();

            for (int i = 0; i < len; i++) {
                // Start from the end of the string and move backwards
                int reverseIndex = len - 1 - i;
                int colorIndex = (sequenceTick + i) % SANIC_COLOR_SEQUENCE.length;
                sb.insert(0, SANIC_COLOR_SEQUENCE[colorIndex] + text.charAt(reverseIndex));
            }

            return sb.toString();
        }



        public static void advanceTick() {
            tick++;
        }
    }




    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ColorTextCycler.advanceTick();
        }
    }







}

