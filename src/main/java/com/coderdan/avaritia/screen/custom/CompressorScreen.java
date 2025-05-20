package com.coderdan.avaritia.screen.custom;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.block.entity.custom.CompressorBlockEntity;
import com.coderdan.avaritia.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class CompressorScreen extends AbstractContainerScreen<CompressorMenu> {

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/gui/compressor/compressor.png");

    private final CompressorBlockEntity blockEntity;

    public CompressorScreen(CompressorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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


        int compressionProgress = blockEntity.getCompressionProgress();
        int progressSingularityIcon = blockEntity.storedItemCount;
        int compressionProgressIconMax = blockEntity.processDurration;
        int progressSingularityIconMax = blockEntity.requiredAmount;



        int progressArrowXWidth = (int)((compressionProgress / (float)compressionProgressIconMax) * 22);
        int SingularityIconYHeight = (int)((progressSingularityIcon / (float)progressSingularityIconMax) * 15);

        System.out.println(SingularityIconYHeight + "/" + 15 + " - "+ progressSingularityIcon + "/" + progressSingularityIconMax);


        if(progressArrowXWidth > 0)
        {
            pGuiGraphics.blit(GUI_TEXTURE, x + 62, y + 35, 176, 0, progressArrowXWidth, 15);
        }

        pGuiGraphics.blit(GUI_TEXTURE, x + 90, y + 35, 176, 16, 15, SingularityIconYHeight);

        updateGhostSlots();


    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick); // draw background
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }


    private void updateGhostSlots() {

        //36 example input - ModNonHighlightableSlotHandler
        //39 example output - ModNonHighlightableSlotHandler

        ItemStack exampleInput = menu.blockEntity.getCachedInputExample();
        ItemStack exampleOutput = menu.blockEntity.getCachedOutputExample();

        //System.out.println(exampleInput.getDisplayName().getString());

        ModNonHighlightableSlotHandler exampleInputSlot = (ModNonHighlightableSlotHandler) menu.slots.get(36);
        exampleInputSlot.setGhostItem(exampleInput.copy());
        menu.slots.get(36).setChanged();

        ModNonHighlightableSlotHandler exampleOutputSlot = (ModNonHighlightableSlotHandler) menu.slots.get(39);
        exampleOutputSlot.setGhostItem(exampleOutput.copy());
        menu.slots.get(39).setChanged();
    }




    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // Centered title
        int titleWidth = this.font.width(this.title);
        int centerX = (this.imageWidth - titleWidth) / 2;
        pGuiGraphics.drawString(this.font, this.title, centerX, this.titleLabelY, 0x404040, false);

        // "Inventory" label (left-aligned like vanilla)
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);


        int progress = menu.data.get(0);
        int goal = menu.data.get(1);

        int compressionProgress = blockEntity.getCompressionProgress();

        //System.out.println(compressionProgress);

        // Centered progress text


        String progressText = "" + progress + "/" + goal;

        int progressWidth = this.font.width(progressText);
        int progressX = (this.imageWidth - progressWidth) / 2;
        int progressY = 63;

        if(progress > 0)
        {
            if(goal > 0)
            {
                //System.out.println(progress + "goal");
                pGuiGraphics.drawString(this.font, progressText, progressX, progressY, 0x404040, false);
            }
        }


    }
}
