package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.util.AnimatedMask;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.Objects;

@Mixin(ItemRenderer.class)
public class SwordRendererMixin {

    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");

    float shaderZoom = 1;
    @Redirect(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void renderSwordWithOverlay(ItemRenderer instance, BakedModel model, ItemStack stack, int light, int overlay, PoseStack poseStack, VertexConsumer defaultBuffer) {
        if (stack.getItem() == ModItems.INFINITY_SWORD.get()) {
            // Render base sword
            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;


            if(shader != null)
            {
                boolean isInventory = Minecraft.getInstance().screen != null;
                boolean isMaxLight = light == 15728880;

                boolean isHUD = !isInventory && isMaxLight;
                boolean isTrulyInventory = isInventory && isMaxLight;

                shaderZoom = isTrulyInventory ? 25f :
                        isHUD             ? 25f :
                                Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.8f : 1.25f;

                Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);


                if (isTrulyInventory || isHUD) {
                    float yaw = 0;
                    float pitch = 0;

                    Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                    Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
                }
                else
                {
                    float yaw = Minecraft.getInstance().player.getYRot();
                    float pitch = Minecraft.getInstance().player.getXRot();

                    Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                    Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
                }



                float partialTicks = Minecraft.getInstance().getFrameTimeNs();
                float gameTime = Minecraft.getInstance().level.getGameTime() + partialTicks;

                int frameCount = 8;
                int frameDuration = 75; // ms per frame
                int currentFrame = (int)((System.currentTimeMillis() / frameDuration) % frameCount);


                Objects.requireNonNull(shader.getUniform("GameTime")).set(gameTime);
                Objects.requireNonNull(shader.getUniform("currentFrame")).set((int) currentFrame);
                Objects.requireNonNull(shader.getUniform("frameCount")).set((int) frameCount);


                //System.out.println("yaw " + yaw + " pitch " + pitch + " -pitch " + -pitch);

            }


            // Shader overlay
            VertexConsumer vc = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(ModRenderTypes.infinityVoidSolidShader(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png")));


            // Load the animated mask
            ResourceLocation maskLoc = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/item/tools/infinity_sword/mask.png");
            AnimatedMask animatedMask;

            try {
                animatedMask = new AnimatedMask(maskLoc);
            } catch (IOException e) {
                System.err.println("Failed to load animated mask: " + maskLoc);
                return;
            }

            NativeImage maskImg = animatedMask.getCurrentFrame();
            int width = maskImg.getWidth();
            int height = maskImg.getHeight();

            // Go pixel by pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = maskImg.getPixelRGBA(x, y);
                    int r = (color >> 24) & 0xFF;
                    int g = (color >> 16) & 0xFF;
                    int b = (color >> 8) & 0xFF;

                    if (r > 0 && g > 0 && b > 0) {
                        float fx = x / 16f;
                        float fy = (15 - y) / 16f; // Flip y so 0 is bottom
                        float fz = 0.0001f;

                        float brightness = (r + g + b) / (255f * 3f);
                        float whiteness = (float) Math.pow(brightness, 2f);




                        // Front face
                        poseStack.pushPose();
                        poseStack.scale(1.0f / 16f, 1f / 16f, 1.0f);
                        poseStack.translate(fx * 16f, fy * 16f, fz * 16f);
                        instance.renderQuadList(poseStack, vc, model.getQuads(null, null, RandomSource.create()), stack, light, overlay);
                        poseStack.popPose();

                        // Back face (mirrored Z)
                        poseStack.pushPose();
                        poseStack.scale(1.0f / 16f, 1f / 16f, 1.0f);
                        poseStack.translate(fx * 16f, fy * 16f, -fz * 16f); // flip Z
                        instance.renderQuadList(poseStack, vc, model.getQuads(null, null, RandomSource.create()), stack, light, overlay);
                        poseStack.popPose();



                        if (shader != null)
                        {
                            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set(whiteness); // e.g. 0.0f to fade out completely
                            //System.out.println(whiteness + " Whiteness");
                        }

                    }
                }
            }

            animatedMask.close(); // clean up
        } else if(stack.getItem() == ModItems.INFINITY_BOW.get())
        {
            // Render base sword
            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;


            if(shader != null)
            {
                boolean isInventory = Minecraft.getInstance().screen != null;
                boolean isMaxLight = light == 15728880;

                boolean isHUD = !isInventory && isMaxLight;
                boolean isTrulyInventory = isInventory && isMaxLight;

                shaderZoom = isTrulyInventory ? 25f :
                        isHUD             ? 25f :
                                Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 1.5f : 1.25f;

                Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);

                if (isTrulyInventory || isHUD) {
                    float yaw = 0;
                    float pitch = 0;

                    Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                    Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
                }
                else
                {
                    float yaw = Minecraft.getInstance().player.getYRot();
                    float pitch = Minecraft.getInstance().player.getXRot();

                    Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                    Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
                }

                double systemTime = System.nanoTime() / 1_000_000_000.0;
                Objects.requireNonNull(shader.getUniform("GameTime")).set((float) systemTime);



                int frameCount = 8;
                int frameDuration = 75; // ms per frame
                int currentFrame = (int)((System.currentTimeMillis() / frameDuration) % frameCount);


                Objects.requireNonNull(shader.getUniform("GameTime")).set((float)systemTime);
                //System.out.println("System Time: " + systemTime);

                Objects.requireNonNull(shader.getUniform("currentFrame")).set((int) currentFrame);
                Objects.requireNonNull(shader.getUniform("frameCount")).set((int) frameCount);



                //System.out.println("yaw " + yaw + " pitch " + pitch + " -pitch " + -pitch);

            }


            // Shader overlay
            VertexConsumer vc = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(ModRenderTypes.infinityVoidSolidShader(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png")));


            String stage = "idle_mask";


            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isUsingItem()
                    && Minecraft.getInstance().player.getUseItem() == stack) {

                int elapsed = stack.getUseDuration(Minecraft.getInstance().player)
                        - Minecraft.getInstance().player.getUseItemRemainingTicks();

                float pull = elapsed / 20f; // Minecraft uses seconds (20 ticks = 1 sec)

                //System.out.println("[AVARITIA DEBUG] Pull time: " + pull + " (" + elapsed + " ticks)");

                //System.out.println(pull + " pull");
                if (pull >= 0.35f * (0.9f / 0.9f)) stage = "pull_2_mask";        // >= 0.3
                else if (pull >= 0.225f) stage = "pull_1_mask";
                else if (pull > 0) stage = "pull_0_mask";


            }


            ResourceLocation maskLoc = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/item/tools/infinity_bow/" + stage + ".png");

            AnimatedMask animatedMask;

            try {
                animatedMask = new AnimatedMask(maskLoc);
            } catch (IOException e) {
                System.err.println("Failed to load animated mask: " + maskLoc);
                return;
            }

            NativeImage maskImg = animatedMask.getCurrentFrame();
            int width = maskImg.getWidth();
            int height = maskImg.getHeight();

            // Go pixel by pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = maskImg.getPixelRGBA(x, y);
                    int r = (color >> 24) & 0xFF;
                    int g = (color >> 16) & 0xFF;
                    int b = (color >> 8) & 0xFF;

                    float whiteness = (r + g + b) / (255f * 3f); // average brightness



                    if (r > 0 && g > 0 && b > 0) {
                        float fx = x / 16f;
                        float fy = (15 - y) / 16f; // Flip y so 0 is bottom
                        float fz = 0.0001f;

                        // Front face
                        poseStack.pushPose();
                        poseStack.scale(1.0f / 16f, 1f / 16f, 1.0f);
                        poseStack.translate(fx * 16f, fy * 16f, fz * 16f);
                        instance.renderQuadList(poseStack, vc, model.getQuads(null, null, RandomSource.create()), stack, light, overlay);
                        poseStack.popPose();

                        // Back face (mirrored Z)
                        poseStack.pushPose();
                        poseStack.scale(1.0f / 16f, 1f / 16f, 1.0f);
                        poseStack.translate(fx * 16f, fy * 16f, -fz * 16f); // flip Z

                        instance.renderQuadList(poseStack, vc, model.getQuads(null, null, RandomSource.create()), stack, light, overlay);
                        poseStack.popPose();

                        if (shader != null)
                        {
                            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set(whiteness * 2); // e.g. 0.0f to fade out completely
                            //System.out.println(whiteness + " Whiteness");
                        }

                    }
                }
            }

            animatedMask.close(); // clean up
        }

        else {
            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);
        }
    }

}




