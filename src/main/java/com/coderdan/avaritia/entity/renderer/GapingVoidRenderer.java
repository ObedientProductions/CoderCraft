package com.coderdan.avaritia.entity.renderer;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.entity.custom.GapingVoidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GapingVoidRenderer<T extends GapingVoidEntity> extends EntityRenderer<T> {
    public GapingVoidRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0f;
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {


        float progress = (pEntity.tickCount + pPartialTick) / (float) GapingVoidEntity.MAX_AGE;
        float scale = Mth.clamp(progress, 0f, 1f) * 5f;




        pPoseStack.pushPose();
        pPoseStack.translate(0, 1.0, 0); // elevate if needed
        pPoseStack.scale(scale, scale, scale);
        pPoseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());


        VertexConsumer consumer = pBufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID,"textures/entity/voidhalo.png")));
        Matrix4f matrix = pPoseStack.last().pose();

        // Draw a black transparent ball (or quad for simplicity)
        // For a real sphere, use a custom model

        float size = 1f;

        // Basic square facing camera
        PoseStack.Pose pose = pPoseStack.last();
        Matrix4f mat = pose.pose();


        consumer.addVertex(matrix, -size, size, 0.0f)
                .setUv(0.0f, 0.0f).setColor(0, 0, 0, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        consumer.addVertex(matrix, size, size, 0.0f)
                .setUv(1.0f, 0.0f).setColor(0, 0, 0, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        consumer.addVertex(matrix, size, -size, 0.0f)
                .setUv(1.0f, 1.0f).setColor(0, 0, 0, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        consumer.addVertex(matrix, -size, -size, 0.0f)
                .setUv(0.0f, 1.0f).setColor(0, 0, 0, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);


        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID,"textures/entity/voidhalo.png"); // placeholder texture
    }
}
