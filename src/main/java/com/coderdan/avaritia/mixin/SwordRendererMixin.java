package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.item.ModItems;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Mixin(ItemRenderer.class)
public class SwordRendererMixin {

    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");

    float shaderZoom = 1;
    float gameTime = 0;
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

            ShaderInstance shader = ModRenderTypes.infinityVoidShader;


            if(shader != null)
            {
                shaderZoom = Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.3f : 1f;
                Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);

                float yaw = Minecraft.getInstance().player.getYRot();
                float pitch = Minecraft.getInstance().player.getXRot();

                Objects.requireNonNull(shader.getUniform("CameraYaw")).set((float) yaw);
                Objects.requireNonNull(shader.getUniform("CameraPitch")).set((float) pitch);


                System.out.println("yaw " + yaw + " pitch " + pitch + " -pitch " + -pitch);

            }


            // Shader overlay
            VertexConsumer vc = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(ModRenderTypes.infinityVoidShader(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png")));

            // Load the mask
            ResourceLocation maskLoc = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/item/tools/infinity_sword/sword_mask.png");
            NativeImage maskImg;
            try {
                maskImg = NativeImage.read(Minecraft.getInstance().getResourceManager().open(maskLoc));
            } catch (IOException e) {
                System.err.println("Failed to load mask: " + maskLoc);
                return;
            }

            int width = maskImg.getWidth();
            int height = maskImg.getHeight();

            // Go pixel by pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int color = maskImg.getPixelRGBA(x, y);
                    int r = (color >> 24) & 0xFF;
                    int g = (color >> 16) & 0xFF;
                    int b = (color >> 8) & 0xFF;

                    if (r == 255 && g == 255 && b == 255) {
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

                    }
                }
            }

            maskImg.close(); // clean up
        } else {
            instance.renderModelLists(model, stack, light, overlay, poseStack, defaultBuffer);
        }
    }
}

