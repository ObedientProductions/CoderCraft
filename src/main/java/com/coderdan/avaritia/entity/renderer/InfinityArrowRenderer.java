package com.coderdan.avaritia.entity.renderer;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.entity.custom.InfinityArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfinityArrowRenderer extends ArrowRenderer<InfinityArrowEntity> {
    public InfinityArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/entity/heavenarrow.png");

    @Override
    public ResourceLocation getTextureLocation(InfinityArrowEntity pEntity) {
        return TEXTURE;
    }







}
