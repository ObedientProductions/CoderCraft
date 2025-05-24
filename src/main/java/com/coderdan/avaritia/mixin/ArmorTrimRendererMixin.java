package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class ArmorTrimRendererMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {

    String patternIdName = Avaritia.MOD_ID + ":heavens_mark";
    String materialIdName = Avaritia.MOD_ID + ":infinity_ingot";

    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    private void injectCustomTrimRender(
            PoseStack poseStack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int light, A model, CallbackInfo ci
    ) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorItem)) return;

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null || !trim.pattern().isBound()) return;

        String patternId = trim.pattern().getRegisteredName();
        String materialId = trim.material().get().ingredient().getRegisteredName();

        if (!patternId.equals(patternIdName)) return;

        if (!materialId.equals(materialIdName)) return;

        // Custom animated trim texture render
        int frame = getMcmetaFrame();
        ResourceLocation texture;
        if (slot == EquipmentSlot.LEGS) {
            texture = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/armor_trim_frames/heavens_mark_leggings" + frame + ".png");
        } else {
            texture = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/armor_trim_frames/heavens_mark" + frame + ".png");
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.armorCutoutNoCull(texture));
        Model animatedModel = ForgeHooksClient.getArmorModel(entity, stack, slot, model);
        animatedModel.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY);

    }


    @ModifyVariable(
            method = "renderArmorPiece",
            at = @At(value = "STORE"),
            ordinal = 0 // This targets the ArmorTrim variable assignment
    )

    private ArmorTrim filterCustomTrim(ArmorTrim original, PoseStack poseStack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int light, A model) {
        if (original == null || !original.pattern().isBound() || !original.material().isBound()) return original;

        String patternId = original.pattern().getRegisteredName();
        String materialId = original.material().get().ingredient().getRegisteredName();

        // Cancel default rendering only for specific trim + material combo
        if (patternId.equals(patternIdName) && materialId.equals(materialIdName)) {
            return null;
        }

        return original;
    }



    private static final int[] FRAME_ORDER = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 7, 6, 5, 4, 3, 2, 1
    };

    private static final int[] FRAME_TIMES = {
            3, 3, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 3
    };

    private static final int TICK_DURATION = 50; // 50ms per tick = ~20TPS

    private static int getMcmetaFrame() {
        long totalElapsed = (System.currentTimeMillis() / TICK_DURATION);
        long currentTick = totalElapsed % totalAnimationLength();

        long accumulator = 0;
        for (int i = 0; i < FRAME_ORDER.length; i++) {
            accumulator += FRAME_TIMES[i];
            if (currentTick < accumulator) return FRAME_ORDER[i];
        }
        return 0;
    }

    private static long totalAnimationLength() {
        long sum = 0;
        for (int t : FRAME_TIMES) sum += t;
        return sum;
    }



}
