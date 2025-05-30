package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.events.ForgeClientEvents;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.util.AnimatedMask;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderTypeHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@Mixin(ItemRenderer.class)
public class SwordRendererMixin {

    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");

    float shaderZoom = 1;
    @Inject(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )

    private void renderSwordWithOverlay(ItemStack stack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack poseStack, MultiBufferSource pBufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {

        if (stack.getItem() == ModItems.INFINITY_SWORD.get()) {

            // Render base sword

            ItemRenderer instance = (ItemRenderer)(Object)this;

            RenderType renderType = RenderTypeHelper.getFallbackItemRenderType(stack, model, false);
            VertexConsumer defaultBuffer = pBufferSource.getBuffer(renderType);


            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);


            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;


            if(shader != null)
            {
                boolean isInventory = Minecraft.getInstance().screen != null;
                boolean isMaxLight = light == 15728880;
                boolean isHUD = !isInventory && isMaxLight;
                boolean isTrulyInventory = isInventory && isMaxLight;
                boolean isGround = pDisplayContext == ItemDisplayContext.GROUND;

                shaderZoom = isTrulyInventory ? 25f :
                        isHUD             ? 25f :
                                isGround          ? 2f:
                                        Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.9f : 1.25f;

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

                        float x0 = fx;
                        float x1 = fx + 1f / 16f;
                        float y0 = fy;
                        float y1 = fy + 1f / 16f;

                        // UV coords assuming full texture space, adjust if you need per-frame offsets
                        float u0 = x / (float)width;
                        float u1 = (x + 1) / (float)width;
                        float v0 = y / (float)height;
                        float v1 = (y + 1) / (float)height;

                        poseStack.pushPose();
                        Matrix4f matrix = poseStack.last().pose();

                        poseStack.translate(0,0,0.5320f);

                        // Render quad
                        vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

                        poseStack.popPose();

                        poseStack.pushPose();
                        Matrix4f matrix2 = poseStack.last().pose();

                        poseStack.translate(0,0,0.4680f);

                        // Render quad
                        vc.addVertex(matrix2, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

                        poseStack.popPose();




                        if (shader != null)
                        {
                            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set(1f); // e.g. 0.0f to fade out completely
                            //System.out.println(whiteness + " Whiteness");
                        }

                    }
                }
            }

            animatedMask.close(); // clean up


        } else if(stack.getItem() == ModItems.INFINITY_BOW.get())
        {

            ItemRenderer instance = (ItemRenderer)(Object)this;

            RenderType renderType = RenderTypeHelper.getFallbackItemRenderType(stack, model, false);
            VertexConsumer defaultBuffer = pBufferSource.getBuffer(renderType);
            // Render base sword
            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);


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

                        float x0 = fx;
                        float x1 = fx + 1f / 16f;
                        float y0 = fy;
                        float y1 = fy + 1f / 16f;

                        // UV coords assuming full texture space, adjust if you need per-frame offsets
                        float u0 = x / (float)width;
                        float u1 = (x + 1) / (float)width;
                        float v0 = y / (float)height;
                        float v1 = (y + 1) / (float)height;

                        poseStack.pushPose();
                        Matrix4f matrix = poseStack.last().pose();

                        poseStack.translate(0,0,0.5320f);

                        // Render quad
                        vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

                        poseStack.popPose();

                        poseStack.pushPose();
                        Matrix4f matrix2 = poseStack.last().pose();

                        poseStack.translate(0,0,0.4680f);

                        // Render quad
                        vc.addVertex(matrix2, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                        vc.addVertex(matrix2, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255)
                                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

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
            return;
        }
    }

}




