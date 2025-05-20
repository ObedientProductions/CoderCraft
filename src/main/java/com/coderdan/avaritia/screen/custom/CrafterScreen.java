package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.Avaritia;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/crafter/crafter_gui.png");

    public CrafterScreen(CrafterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int guiTextureHeight = imageHeight + 10;

        pGuiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, guiTextureHeight);

        pGuiGraphics.blit(GUI_TEXTURE, x + 121, y + 35, 176, 0, 22, 16);
        pGuiGraphics.blit(GUI_TEXTURE, x + 154, y + 54, 176, 16, 7, 6);

        pGuiGraphics.blit(GUI_TEXTURE, pMouseX, pMouseY, 176, 16, 7, 6);
    }

    private static final ResourceLocation PUNCH_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/block/crafter_front.png");

    @Override
    protected void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {




    }
}
