package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.item.custom.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemRenderer.class)
public abstract class ModItemRendererMixin {

    private static ResourceLocation HALO_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo.png");
    private static ResourceLocation HALO_GOLD_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/gold_halo.png");
    private static ResourceLocation HALO128_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo128.png");
    private static ResourceLocation HALO_NOISE_INGOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo_noise.png");
    private static ResourceLocation HALO_NOISE_NUGGET_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo_noise_medium.png");
    private static ResourceLocation HALO_NOISE_PILE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo_noise_soft.png");
    private static ResourceLocation HALO_ULTIMATE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/effects/items/halo_ultimate.png");

    private static ResourceLocation ARMOREYES = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pCombinedLight, int pCombinedOverlay, BakedModel pModel, CallbackInfo ci){


        if (pDisplayContext == ItemDisplayContext.GUI) {
            if (pItemStack.getItem() instanceof NeutronIngotItem) {


                VertexConsumer buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_NOISE_INGOT_TEXTURE));
                renderNeuronHalo(pPoseStack, buffer, 1.1f, 0.325f);
            }
            else if (pItemStack.getItem() instanceof NeutronNuggetItem) {
                VertexConsumer buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_NOISE_NUGGET_TEXTURE));
                renderNeuronHalo(pPoseStack, buffer, 1.1f, 0.262f);
            }
            else if (pItemStack.getItem() instanceof NeutronPileItem) {
                VertexConsumer buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_NOISE_PILE_TEXTURE));
                renderNeuronHalo(pPoseStack, buffer, 1.1f, 0.2f);
            }
            else if (pItemStack.getItem() instanceof UltimateItem) {

                VertexConsumer buffer;

                if(pItemStack.getItem() instanceof EndestPearlItem)
                {
                    buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_TEXTURE));
                }
                else
                {
                    buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_ULTIMATE_TEXTURE));
                }

                renderHalo(pPoseStack, buffer, 1.1f);
                animate(pBufferSource, pPoseStack);

            }

            if (pItemStack.getItem() instanceof SingularityItem) {

                VertexConsumer buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_TEXTURE));
                renderHalo(pPoseStack, buffer, 1.1f);
            }

            if(pItemStack.getItem() instanceof Specialitem)
            {
                VertexConsumer buffer = pBufferSource.getBuffer(ModRenderTypes.modGui(HALO_GOLD_TEXTURE));
                renderHalo(pPoseStack, buffer, 1.1f);
            }

        }
        else if (pItemStack.getItem() instanceof UltimateItem) {
            if (pDisplayContext == ItemDisplayContext.FIXED) {
                animateFixed(pPoseStack);
            }
            else if (pDisplayContext == ItemDisplayContext.GROUND) {
                animateGround(pPoseStack, pBufferSource.getBuffer(RenderType.entityTranslucentEmissive(HALO_TEXTURE)));
            }
            else if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                animateHand(pPoseStack, pBufferSource.getBuffer(RenderType.entityTranslucentEmissive(HALO_TEXTURE)));
            }
            else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                animateGround(pPoseStack, pBufferSource.getBuffer(RenderType.entityTranslucentEmissive(HALO_TEXTURE)));
            }

        }



    }


    private void animate(MultiBufferSource pBufferSource, PoseStack pPoseStack){
        //VertexConsumer buffer = pBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(HALO128_TEXTURE));



        //renderHalo(pPoseStack, buffer, 1.5f);

        float baseScale = 1f;
        float pulseRange = 0.04f; // how much it grows/shrinks
        float speed = 75f;       // duration of one full pulse in ms

        float time = (System.currentTimeMillis() % (long)speed) / speed;
        float scale = baseScale + (float)Math.sin(time * Math.PI * 2) * pulseRange;

        pPoseStack.scale(scale, scale, baseScale);


    }


    private void testRender(MultiBufferSource pBufferSource, PoseStack pPoseStack)
    {
        VertexConsumer buffer = pBufferSource.getBuffer(RenderType.textSeeThrough(ARMOREYES)); // your sprite sheet

        pPoseStack.pushPose();
        Matrix4f matrix = pPoseStack.last().pose();

        float size = 1.0f;



        buffer.addVertex(matrix, -size, -size, 0.0f)
                .setUv(0.0f, 1.0f)
                .setColor(255, 255, 255, 255)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, 1);

        buffer.addVertex(matrix, size, -size, 0.0f)
                .setUv(1.0f, 1.0f)
                .setColor(255, 255, 255, 255)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, 1);

        buffer.addVertex(matrix, size, size, 0.0f)
                .setUv(1.0f, 0.0f)
                .setColor(255, 255, 255, 255)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, 1);

        buffer.addVertex(matrix, -size, size, 0.0f)
                .setUv(0.0f, 0.0f)
                .setColor(255, 255, 255, 255)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, 1);

        pPoseStack.popPose();

    }

    private void animateFixed(PoseStack pPoseStack){

        float baseScale = 1f;
        float pulseRange = 0.04f; // how much it grows/shrinks
        float speed = 75f;       // duration of one full pulse in ms

        float time = (System.currentTimeMillis() % (long)speed) / speed;
        float scale = baseScale + (float)Math.sin(time * Math.PI * 2) * pulseRange;

        pPoseStack.scale(scale, scale, baseScale);
    }


    private void animateGround(PoseStack pPoseStack, VertexConsumer buffer) {
        float amplitude = 0.005f; // how much it moves up/down
        float speed = 75f;       // duration of one full pulse in ms

        float time = (System.currentTimeMillis() % (long) speed) / speed;
        float offset = (float) Math.sin(time * Math.PI * 2) * amplitude;

        pPoseStack.translate(offset, offset, offset); // move up/down on Y axis

        float baseScale = 1f;
        float pulseRange = 0.01f; // how much it grows/shrinks
        float ScaleSpeed = 75f;       // duration of one full pulse in ms

        float Scaletime = (System.currentTimeMillis() % (long)ScaleSpeed) / ScaleSpeed;
        float scale = baseScale + (float)Math.sin(Scaletime * Math.PI * 2) * pulseRange;

        pPoseStack.scale(scale, scale, baseScale);
    }

    private void animateHand(PoseStack pPoseStack, VertexConsumer buffer) {
        float amplitude = 0.001f; // how much it moves up/down
        float speed = 75f;       // duration of one full pulse in ms

        float time = (System.currentTimeMillis() % (long) speed) / speed;
        float offset = (float) Math.sin(time * Math.PI * 2) * amplitude;

        pPoseStack.translate(offset, offset, offset); // move up/down on Y axis

        float baseScale = 1f;
        float pulseRange = 0.01f; // how much it grows/shrinks
        float ScaleSpeed = 75f;       // duration of one full pulse in ms

        float Scaletime = (System.currentTimeMillis() % (long)ScaleSpeed) / ScaleSpeed;
        float scale = baseScale + (float)Math.sin(Scaletime * Math.PI * 2) * pulseRange;

        pPoseStack.scale(scale, scale, baseScale);
    }



    private void renderHaloFixed(PoseStack pPoseStack, VertexConsumer buffer, float size){

        pPoseStack.pushPose();


        pPoseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90f)); // add 90° rotation



        Matrix4f matrix = pPoseStack.last().pose();

        pPoseStack.translate(0,0,0.6);


        float Opacity = 10f;

        buffer.addVertex(matrix, -size,  size, -0.5f).setUv(0, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size,  size, -0.5f).setUv(1, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size, -size, -0.5f).setUv(1, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix, -size, -size, -0.5f).setUv(0, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);


        pPoseStack.popPose();
    }

    private void renderHalo(PoseStack pPoseStack, VertexConsumer buffer, float size){

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        pPoseStack.pushPose();


        Matrix4f matrix = pPoseStack.last().pose();


        float Opacity = 0.95f * 255;

        buffer.addVertex(matrix, -size, -size, -0.5f).setUv(0, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size, -size, -0.5f).setUv(1, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size,  size, -0.5f).setUv(1, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix, -size,  size, -0.5f).setUv(0, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);


        pPoseStack.popPose();

        // --- Clean up after blend ---
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
    }


    private void renderHaloFollow(PoseStack pPoseStack, VertexConsumer buffer, float size){

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        pPoseStack.pushPose();


        Matrix4f matrix = pPoseStack.last().pose();

        // Reset any item/entity rotation (neutralize pose stack)
        pPoseStack.last().pose().identity();
        pPoseStack.last().normal().identity();

        // Make it face the camera only
        pPoseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());




        float Opacity = 0.95f * 255;

        buffer.addVertex(matrix, -size, -size, -0.5f).setUv(0, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size, -size, -0.5f).setUv(1, 1).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix,  size,  size, -0.5f).setUv(1, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        buffer.addVertex(matrix, -size,  size, -0.5f).setUv(0, 0).setColor(0, 0, 0, Opacity).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);


        pPoseStack.popPose();

        // --- Clean up after blend ---
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
    }

    private void renderNeuronHalo(PoseStack pPoseStack, VertexConsumer buffer, float size, float opacity){

        // --- Begin special blend ---
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();



        pPoseStack.pushPose();


        Matrix4f matrix = pPoseStack.last().pose();

        float frameHeight = 32f / 256f; // 0.125
        int totalFrames = 8; // 256 / 32 = 8 frames
        int frameDuration = 40; // milliseconds per frame (adjust this for speed)

        long time = System.currentTimeMillis();
        int currentFrame = (int)((time / frameDuration) % totalFrames);

        float vOffset = currentFrame * frameHeight;

        int alpha = (int)(opacity * 255);

        //Minecraft.getInstance().getTextureManager().bindForSetup(HALO_TEXTURE);




        buffer.addVertex(matrix, -size, -size, -0.5f)
                .setUv(0f, vOffset + frameHeight)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        buffer.addVertex(matrix, size, -size, -0.5f)
                .setUv(1f, vOffset + frameHeight)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        buffer.addVertex(matrix, size, size, -0.5f)
                .setUv(1f, vOffset)
                .setColor(255, 255, 255, alpha)
                .setUv2(240,240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        buffer.addVertex(matrix, -size, size, -0.5f)
                .setUv(0f, vOffset)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        pPoseStack.popPose();

        // --- Clean up after blend ---
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();

    }




}





