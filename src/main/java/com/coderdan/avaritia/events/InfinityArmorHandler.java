package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.item.custom.InfinityBootsItem;
import com.coderdan.avaritia.item.custom.ModInfinityArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfinityArmorHandler {

    private static final ResourceLocation INFINITY_SPEED_ID = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "infinity_boots_speed");
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        LivingEntity player = event.player;

        boolean fullSet = Arrays.stream(EquipmentSlot.values())
                .filter(slot -> slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
                .allMatch(slot -> player.getItemBySlot(slot).getItem() instanceof ModInfinityArmorItem);

        if (fullSet) {
            Scoreboard scoreboard = player.level().getScoreboard();
            Team team = scoreboard.getPlayersTeam(player.getScoreboardName());

            if (team == null || !team.getName().equals("avaritia_infinity_team")) {
                PlayerTeam customTeam = scoreboard.addPlayerTeam("avaritia_infinity_team");
                customTeam.setColor(ChatFormatting.WHITE);
                scoreboard.addPlayerToTeam(player.getScoreboardName(), customTeam);
            }

            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1, 1), player);
            player.setGlowingTag(true);
        } else {
            Team current = player.level().getScoreboard().getPlayersTeam(player.getScoreboardName());
            if (current != null && current.getName().equals("avaritia_infinity_team")) {
                player.level().getScoreboard().removePlayerFromTeam(player.getScoreboardName(), (PlayerTeam) current);
            }

            player.setGlowingTag(false);
        }

        // Infinity Boots Speed Boost
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof InfinityBootsItem) {
            if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(INFINITY_SPEED_ID)) {
                AttributeModifier speedBoost = new AttributeModifier(
                        INFINITY_SPEED_ID,
                        0.2f, // 2x total: 0.2 base + default 0.1 = 0.5 (5x base 0.1)
                        AttributeModifier.Operation.ADD_VALUE
                );
                Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(speedBoost);
            }
        } else {
            // Remove boost if not wearing boots
            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(INFINITY_SPEED_ID);
        }
    }
}