package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    public static float DEBUG_SIZE = 1f; // starts smaller
    public static Vec3 DEBUG_OFFSET = new Vec3(0f, 0.1754f, -0.1510f);
    public static float DEBUG_WIDTH_MULTIPLIER = 0.07f;


    private static final float OFFSET_STEP = 0.001f;
    private static final float SIZE_STEP = 0.01f;
    private static final float WIDTH_STEP = 0.001f;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().screen != null) return;

        long window = Minecraft.getInstance().getWindow().getWindow();
        boolean updated = false;

        // Move offset (X/Y)
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_UP)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(0, OFFSET_STEP, 0);
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_DOWN)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(0, -OFFSET_STEP, 0);
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(OFFSET_STEP, 0, 0);
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(-OFFSET_STEP, 0, 0);
            updated = true;
        }

        // Move offset (Z)
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_PAGE_UP)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(0, 0, OFFSET_STEP);
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_PAGE_DOWN)) {
            DEBUG_OFFSET = DEBUG_OFFSET.add(0, 0, -OFFSET_STEP);
            updated = true;
        }

        // Adjust size
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_GRAVE_ACCENT)) { // `~` key
            DEBUG_SIZE += SIZE_STEP;
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_1)) {
            DEBUG_SIZE = Math.max(SIZE_STEP, DEBUG_SIZE - SIZE_STEP);
            updated = true;
        }


        // Adjust width multiplier
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_BRACKET)) { // [
            DEBUG_WIDTH_MULTIPLIER = Math.max(WIDTH_STEP, DEBUG_WIDTH_MULTIPLIER - WIDTH_STEP);
            updated = true;
        }
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_BRACKET)) { // ]
            DEBUG_WIDTH_MULTIPLIER += WIDTH_STEP;
            updated = true;
        }


        if (updated) {
            System.out.printf("Size: %.4f | Width: %.2f | Offset: %.4f, %.4f, %.4f%n",
                    DEBUG_SIZE, DEBUG_WIDTH_MULTIPLIER, DEBUG_OFFSET.x, DEBUG_OFFSET.y, DEBUG_OFFSET.z);

        }
    }


}
