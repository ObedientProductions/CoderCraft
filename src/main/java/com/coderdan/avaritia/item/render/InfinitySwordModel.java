package com.coderdan.avaritia.item.render;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfinitySwordModel extends BakedModelWrapper<BakedModel> {

    private static final ResourceLocation LAYER0_TEX = ResourceLocation.parse("avaritia:item/tools/infinity_sword/sword_layer_0");
    private final RenderType bladeRenderType = ModRenderTypes.infinityVoidShader(LAYER0_TEX);

    private final List<BakedQuad> bladeQuads = new ArrayList<>();
    private final List<BakedQuad> otherQuads = new ArrayList<>();

    public InfinitySwordModel(BakedModel originalModel) {
        super(originalModel);

        List<BakedQuad> allQuads = originalModel.getQuads(null, null, RandomSource.create());

        for (BakedQuad quad : allQuads) {
            ResourceLocation tex = quad.getSprite().contents().name();
            if (tex.equals(LAYER0_TEX)) {
                bladeQuads.add(quad);
            } else {
                otherQuads.add(quad);
            }
        }
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
        List<RenderType> types = new ArrayList<>();
        types.add(bladeRenderType); // render blade separately
        types.addAll(originalModel.getRenderTypes(stack, fabulous));
        return types;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        RenderType current = RenderTypeRegistry.getCurrent(); // Use ThreadLocal trick
        System.out.println("[INFINITY SWORD] Current RenderType: " + current);
        System.out.println("[INFINITY SWORD] Matching blade: " + bladeRenderType);


        if (current != null && current.equals(bladeRenderType)) {
            return bladeQuads;
        } else {
            return otherQuads;
        }
    }
}



