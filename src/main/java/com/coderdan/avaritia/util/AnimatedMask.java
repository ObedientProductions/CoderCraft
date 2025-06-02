package com.coderdan.avaritia.util;

import com.google.gson.*;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AnimatedMask {
    private final ResourceLocation texture;
    private final NativeImage image;
    private final boolean isAnimated;
    private final int frameTime;
    private final List<Integer> frames;
    private final int frameHeight;
    private final long startTime;

    public AnimatedMask(ResourceLocation texture) throws IOException {
        this.texture = texture;
        this.image = NativeImage.read(Minecraft.getInstance().getResourceManager().open(texture));
        this.frameHeight = image.getHeight();
        this.startTime = System.currentTimeMillis();

        // Try to read .mcmeta file
        List<Integer> tempFrames = null;
        int tempFrameTime = 1;
        boolean animated = false;

        String mcmetaPath = texture.getPath() + ".mcmeta";
        try (InputStream metaStream = Minecraft.getInstance().getResourceManager().open(
                ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), mcmetaPath))) {

            JsonObject json = JsonParser.parseReader(new java.io.InputStreamReader(metaStream)).getAsJsonObject();
            JsonObject anim = json.getAsJsonObject("animation");

            tempFrameTime = anim.has("frametime") ? anim.get("frametime").getAsInt() : 1;
            if (anim.has("frames")) {
                JsonArray rawFrames = anim.getAsJsonArray("frames");
                tempFrames = new java.util.ArrayList<>();
                for (JsonElement e : rawFrames) {
                    tempFrames.add(e.getAsInt());
                }
            } else {
                int frameCount = frameHeight / 16;
                tempFrames = java.util.stream.IntStream.range(0, frameCount).boxed().toList();
            }

            animated = true;
        } catch (IOException ignored) {
            // No .mcmeta file found — treat as static
            animated = false;
        }

        this.isAnimated = animated;
        this.frameTime = tempFrameTime;
        this.frames = tempFrames;
    }

    public AnimatedMask(ResourceLocation texture, int imageWidth) throws IOException {
        this.texture = texture;
        this.image = NativeImage.read(Minecraft.getInstance().getResourceManager().open(texture));
        this.frameHeight = image.getHeight();
        this.startTime = System.currentTimeMillis();

        // Try to read .mcmeta file
        List<Integer> tempFrames = null;
        int tempFrameTime = 1;
        boolean animated = false;

        String mcmetaPath = texture.getPath() + ".mcmeta";
        try (InputStream metaStream = Minecraft.getInstance().getResourceManager().open(
                ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), mcmetaPath))) {

            JsonObject json = JsonParser.parseReader(new java.io.InputStreamReader(metaStream)).getAsJsonObject();
            JsonObject anim = json.getAsJsonObject("animation");

            tempFrameTime = anim.has("frametime") ? anim.get("frametime").getAsInt() : 1;
            if (anim.has("frames")) {
                JsonArray rawFrames = anim.getAsJsonArray("frames");
                tempFrames = new java.util.ArrayList<>();
                for (JsonElement e : rawFrames) {
                    tempFrames.add(e.getAsInt());
                }
            } else {
                int frameCount = frameHeight / imageWidth;
                tempFrames = java.util.stream.IntStream.range(0, frameCount).boxed().toList();
            }

            animated = true;
        } catch (IOException ignored) {
            // No .mcmeta file found — treat as static
            animated = false;
        }

        this.isAnimated = animated;
        this.frameTime = tempFrameTime;
        this.frames = tempFrames;
    }

    public NativeImage getCurrentFrame() {
        if (!isAnimated) {
            return image;
        }

        long gameTicks = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        int index = (int) ((gameTicks / frameTime) % frames.size());

        int yOffset = frames.get(index) * 16;

        NativeImage frame = new NativeImage(image.getWidth(), 16, false);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < 16; y++) {
                frame.setPixelRGBA(x, y, image.getPixelRGBA(x, y + yOffset));
            }
        }

        int frameValue = frames.get(index);
       // System.out.println("Index: " + index + ", Frame: " + frameValue + " (yOffset = " + (frameValue * 16) + ")");


        return frame;
    }


    public NativeImage getCurrentFrameFromHeight(int height) {
        if (!isAnimated) return image;

        int totalFrames = image.getHeight() / height;
        long gameTicks = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        int index = (int) ((gameTicks / frameTime) % frames.size());


        // Clamp frame index to valid bounds

        int frameIndex = Math.min(frames.get(index), totalFrames - 1);

        int frameValue = frames.get(index);
        //System.out.println("Index: " + index + ", Frame: " + frameValue + " (yOffset = " + (frameValue * 16) + ")");


        int yOffset = frameIndex * height;

        NativeImage frame = new NativeImage(image.getWidth(), height, false);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < height; y++) {
                frame.setPixelRGBA(x, y, image.getPixelRGBA(x, y + yOffset));
            }
        }
        return frame;
    }

    public NativeImage getCurrentFrameFromHeight(int height, boolean doubleSpeed) {
        if (!isAnimated) return image;

        float speed = doubleSpeed ? 2f : 1f;

        int totalFrames = image.getHeight() / height;
        long adjustedTime = (long) ((System.currentTimeMillis() - startTime) * speed);
        long gameTicks = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0;
        int index = (int) ((gameTicks / frameTime) % frames.size());



        // Clamp frame index to valid bounds

        int frameIndex = Math.min(frames.get(index), totalFrames - 1);

        //System.out.println(index + " frame index");

        int yOffset = frameIndex * height;

        NativeImage frame = new NativeImage(image.getWidth(), height, false);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < height; y++) {
                frame.setPixelRGBA(x, y, image.getPixelRGBA(x, y + yOffset));
            }
        }
        return frame;
    }



    public void close() {
        if (isAnimated) {
            image.close();
        }
    }







}
