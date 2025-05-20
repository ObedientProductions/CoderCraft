package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.ExtremeCraftingTableBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

public class ExtremeCraftingScreen extends AbstractContainerScreen<ExtremeCraftingMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/extreme_crafting/dire_crafting_gui.png");

    private final ExtremeCraftingTableBlockEntity blockEntity;

    public ExtremeCraftingScreen(ExtremeCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.blockEntity = pMenu.blockEntity;
        this.imageWidth = 256;
        this.imageHeight = 256;

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int guiTextureHeight = imageHeight;

        pGuiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, guiTextureHeight);



    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick); // draw background
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }





    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // Centered title
        int titleWidth = this.font.width(this.title);
        int centerX = (this.imageWidth - titleWidth) / 2;
        //pGuiGraphics.drawString(this.font, this.title, centerX, this.titleLabelY, 0x404040, false);

        // "Inventory" label (left-aligned like vanilla)
        //pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);


    }
}
