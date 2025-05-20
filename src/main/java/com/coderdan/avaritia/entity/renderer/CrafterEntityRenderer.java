package com.coderdan.avaritia.entity.renderer;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.CrafterBlockEntity;
import com.coderdan.avaritia.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CrafterEntityRenderer implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<CrafterBlockEntity> {

    public CrafterEntityRenderer(BlockEntityRendererProvider.Context context)
    {

    }


    private int getLightLevel(Level level, BlockPos pos)
    {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    @Override
    public void render(CrafterBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        //ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        //ItemStack stack = pBlockEntity.inventory.getStackInSlot(0);

        //pPoseStack.pushPose();
        //pPoseStack.translate(0.5f, 1.15f, 0.5f);
        //pPoseStack.scale(0.5f, 0.5f, 0.5f);
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getRenderingRotation()));

        //itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 1);
        //pPoseStack.popPose();







        //ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(avaritia.MOD_ID,"textures/gui/crafter/crafter_gui.png");

        //RenderType BREEZETEST = RenderType.entityTranslucent(LOCATION);



        //VertexConsumer vertexConsumer = pBufferSource.getBuffer(BREEZETEST);


        //pPoseStack.pushPose();
        //pPoseStack.translate(0.5f, 1.15f, 0.5f);
        //pPoseStack.scale(0.5f, 0.5f, 0.5f);
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getRenderingRotation()));

        //Matrix4f matrix = pPoseStack.last().pose();

        // FRONT
        //vertexConsumer.addVertex(matrix, -0.5f, -0.5f, -0.5f).setUv(0, 1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        //vertexConsumer.addVertex(matrix,  0.5f, -0.5f, -0.5f).setUv(1, 1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        //vertexConsumer.addVertex(matrix,  0.5f,  0.5f, -0.5f).setUv(1, 0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);
        //vertexConsumer.addVertex(matrix, -0.5f,  0.5f, -0.5f).setUv(0, 0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, -1);

        //pPoseStack.popPose();




    }
}
