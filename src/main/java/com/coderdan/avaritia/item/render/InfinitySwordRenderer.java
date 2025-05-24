package com.coderdan.avaritia.item.render;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class InfinitySwordRenderer extends BlockEntityWithoutLevelRenderer {

    private final Minecraft mc = Minecraft.getInstance();
    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");

    public InfinitySwordRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        System.out.println("InfinitySwordRenderer CREATED");
    }




    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getItemRenderer().getModel(stack, null, null, 0);

        // Render everything normally
        for (RenderType renderType : model.getRenderTypes(stack, true)) {
            VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer, renderType, true, stack.hasFoil());
            mc.getItemRenderer().renderModelLists(model, stack, light, overlay, poseStack, consumer);
        }

        // Later: insert your custom RenderType logic for specific textures/layers here
    }

}
