package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.CollectorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;

public class CollectorScreen extends AbstractContainerScreen<CollectorMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/neutron_collector/neutron_collector_gui.png");

    private final CollectorBlockEntity blockEntity;

    public CollectorScreen(CollectorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.blockEntity = pMenu.blockEntity;
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
        pGuiGraphics.drawString(this.font, this.title, centerX, this.titleLabelY, 0x404040, false);

        // "Inventory" label (left-aligned like vanilla)
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);


        // Your new centered label above the inventory

        float progress = blockEntity.getProgress();
        DecimalFormat df = new DecimalFormat("0.#");
        float result = Float.parseFloat(df.format((float) progress)); //to fix visual bug


        String customLabel = "Progress: " + result + "%"; // whatever you want it to say
        int customWidth = this.font.width(customLabel);
        int customCenterX = (this.imageWidth - customWidth) / 2;
        int customY = this.inventoryLabelY - 12; // 10 pixels above the inventory label
        pGuiGraphics.drawString(this.font, customLabel, customCenterX, customY, 0x404040, false);

    }
}
