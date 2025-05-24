package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.events.ForgeClientEvents;
import com.coderdan.avaritia.item.ModItems;
import com.coderdan.avaritia.item.custom.ModInfinityArmorItem;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.TimeZone;

import static org.openjdk.nashorn.api.tree.Parser.create;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorRendererMixin <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static ResourceLocation TXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing.png");
    private static ResourceLocation TXTMASK = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask_wings.png");

    private static ResourceLocation VOIDTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask.png");
    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");

    public ArmorRendererMixin(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }



    boolean hasInfinityArmor(T pLivingEntity)
    {
        ItemStack helmetStack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestStack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack pantsStack = pLivingEntity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack bootsStack = pLivingEntity.getItemBySlot(EquipmentSlot.FEET);


        boolean helm = helmetStack.getItem() instanceof ModInfinityArmorItem;
        boolean chest = chestStack.getItem() instanceof ModInfinityArmorItem;
        boolean legs = pantsStack.getItem() instanceof ModInfinityArmorItem;
        boolean feet = bootsStack.getItem() instanceof ModInfinityArmorItem;

        return helm || chest || legs || feet;
    }


    float time = 0;


    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    private void onRender(PoseStack pPoseStack, MultiBufferSource pBufferSource, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) {

        if (ModRenderTypes.infinityVoidShader != null) {
            ShaderInstance shader = ModRenderTypes.infinityVoidShader;

            float partialTicks = Minecraft.getInstance().getFrameTimeNs();
            float gameTime = Minecraft.getInstance().level.getGameTime() + partialTicks;

            int frameCount = 8;
            int frameDuration = 75; // ms per frame
            int currentFrame = (int)((System.currentTimeMillis() / frameDuration) % frameCount);

            Objects.requireNonNull(shader.getUniform("GameTime")).set(gameTime);
            Objects.requireNonNull(shader.getUniform("currentFrame")).set((int) currentFrame);
            Objects.requireNonNull(shader.getUniform("frameCount")).set((int) frameCount);
        }



        if (pSlot != EquipmentSlot.CHEST) return;

        ItemStack chestStack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);


        //renderArmorEffects(pPoseStack, pModel, pBufferSource); //for testing

        if (!(pLivingEntity instanceof Player player)) return;

        if (chestStack.getItem() instanceof ModInfinityArmorItem && hasInfinityArmor((T) player)) {
            if (player.getAbilities().flying || player.isFallFlying()) {
                renderWings(pPoseStack, pModel, pBufferSource);
            }

            renderArmorEffects(pPoseStack, pModel, pBufferSource);
        }

    }




    void renderWings(PoseStack pPoseStack, A pModel, MultiBufferSource pBufferSource) {


        if (ModRenderTypes.babyShader(TXT, TXTMASK) == null) return;

        time += 0.05f;

        Objects.requireNonNull(ModRenderTypes.babyShader.getUniform("time")).set(time);

        VertexConsumer vc = pBufferSource.getBuffer(ModRenderTypes.babyShader(TXT, TXTMASK));

        ModelPart body = pModel.body;



        float wingOffset = 0.0f; // spacing from body
        float zWingOffset = 0.12f;
        float yWingOffset = 1.4f;
        float size = 2f;
        float wingSpreadDegrees = 20f;

        // Render RIGHT wing
        pPoseStack.pushPose();

        body.translateAndRotate(pPoseStack); // attach to body
        pPoseStack.translate(-wingOffset, yWingOffset, zWingOffset); // right side
        pPoseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees)); // Rotate outward
        renderWingQuad(pPoseStack, vc, size);
        pPoseStack.popPose();

        // Render LEFT wing
        pPoseStack.pushPose();
        body.translateAndRotate(pPoseStack); // attach to body
        pPoseStack.translate(wingOffset, yWingOffset, zWingOffset); // left side
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees)); // Rotate outward
        pPoseStack.scale(-1.0f, 1.0f, 1.0f); // mirror the quad
        renderWingQuad(pPoseStack, vc, size);
        pPoseStack.popPose();
    }


    void renderWingsVoidEffect(PoseStack pPoseStack, A pModel, MultiBufferSource pBufferSource) {


        if (ModRenderTypes.infinityVoidShader(VOIDSTARSTXT) == null) return;

        time += 0.05f;

        //Objects.requireNonNull(ModRenderTypes.infinityVoidShader.getUniform("time")).set(time);

        VertexConsumer vc = pBufferSource.getBuffer(ModRenderTypes.infinityVoidShader(VOIDSTARSTXT));

        ModelPart body = pModel.body;






        float wingOffset = 0.0f; // spacing from body
        float zWingOffset = 0.12f;
        float yWingOffset = 1.4f;
        float size = 2f;
        float wingSpreadDegrees = 20f;

        // Render RIGHT wing
        pPoseStack.pushPose();

        body.translateAndRotate(pPoseStack); // attach to body
        pPoseStack.translate(-wingOffset, yWingOffset, zWingOffset); // right side
        pPoseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees)); // Rotate outward
        renderWingQuad(pPoseStack, vc, size);
        pPoseStack.popPose();

        // Render LEFT wing
        pPoseStack.pushPose();
        body.translateAndRotate(pPoseStack); // attach to body
        pPoseStack.translate(wingOffset, yWingOffset, zWingOffset); // left side
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees)); // Rotate outward
        pPoseStack.scale(-1.0f, 1.0f, 1.0f); // mirror the quad
        renderWingQuad(pPoseStack, vc, size);
        pPoseStack.popPose();
    }


    void renderArmorEffects(PoseStack pPoseStack, A pModel, MultiBufferSource pBufferSource)
    {
        VertexConsumer vc = pBufferSource.getBuffer(ModRenderTypes.infinityVoidShader(VOIDSTARSTXT));




        ModelPart head = pModel.head;
        ModelPart body = pModel.body;
        ModelPart leftLeg = pModel.leftLeg;
        ModelPart rightLeg = pModel.rightLeg;
        ModelPart leftArm = pModel.leftArm;
        ModelPart rightArm = pModel.rightArm;


        //render other stuff

        //pPoseStack.pushPose();
        //body.translateAndRotate(pPoseStack);
        //renderQuad(pPoseStack, vc, size, new Vec3(0.0f, 0.23f, 0.189f), 34, 22, 4,7, 64, 64); // head
        //pPoseStack.popPose();

        pPoseStack.pushPose();
        body.translateAndRotate(pPoseStack);

        float dsize = 0.0362f;
        Vec3 doffset = new Vec3(0.1170f, 0.1204f, 0.189f);

        renderQuadStrip(pPoseStack, vc, dsize, 7, doffset, 34, 22, 4, 7, 64, 64);
        // Compute the horizontal spacing: one strip width * 3 (2 gaps + 1 strip width)
        float spacing = dsize * 2.14f * 3.0f;

        // New offset 2 pixels away from original strip
        Vec3 nextOffset = doffset.add(-spacing, 0.0f, 0.0f);

        renderQuadStrip(pPoseStack, vc, dsize, 7, nextOffset, 34, 22, 4, 7, 64, 64);






        pPoseStack.popPose();






    }



    private void renderWingQuad(PoseStack pPoseStack, VertexConsumer vc, float size) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();



        vc.addVertex(matrix, -size, -size, 0.0f)
                .setUv(0.0f, 1.0f).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, -size, 0.0f)
                .setUv(1.0f, 1.0f).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, size, 0.0f)
                .setUv(1.0f, 0.0f).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -size, size, 0.0f)
                .setUv(0.0f, 0.0f).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

        RenderSystem.disableBlend();

    }

    private void renderQuad(PoseStack poseStack, VertexConsumer vc, float size, Vec3 offset,
                            float u, float v, float regionWidth, float regionHeight,
                            float textureWidth, float textureHeight) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight) / textureHeight;

        vc.addVertex(matrix, -size, -size, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, -size, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, size, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -size, size, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderQuadStrip(PoseStack poseStack, VertexConsumer vc, float size, int length, Vec3 offset,
                                 float u, float v, float regionWidth, float regionHeight,
                                 float textureWidth, float textureHeight) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = size * 2 * length;

        float width = size * 1.07f;


        vc.addVertex(matrix, -width, -size, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size + totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, -size + totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);


        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderQuadStripReversed(PoseStack poseStack, VertexConsumer vc, float size, int length, Vec3 offset,
                                 float u, float v, float regionWidth, float regionHeight,
                                 float textureWidth, float textureHeight) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = size * 2 * length;

        float width = size * 1.07f;

        vc.addVertex(matrix, -width, -size + totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size + totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, -size, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);




        RenderSystem.disableBlend();
        poseStack.popPose();
    }




    private void renderWingQuadAnimated(PoseStack pPoseStack, VertexConsumer vc, float size, float width, float height, float frameDuration) {
        Matrix4f matrix = pPoseStack.last().pose();
        RenderSystem.enableBlend();

        float frameHeight = width / height; // 0.125
        int totalFrames = (int) (height / width); // 256 / 32 = 8 frames

        long time = System.currentTimeMillis();
        int currentFrame = (int)((time / frameDuration) % totalFrames);

        float vOffset = currentFrame * frameHeight;

        int alpha = (int)(255);




        vc.addVertex(matrix, -size, -size, -0.5f)
                .setUv(0f, vOffset + frameHeight)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        vc.addVertex(matrix, size, -size, -0.5f)
                .setUv(1f, vOffset + frameHeight)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        vc.addVertex(matrix, size, size, -0.5f)
                .setUv(1f, vOffset)
                .setColor(255, 255, 255, alpha)
                .setUv2(240,240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        vc.addVertex(matrix, -size, size, -0.5f)
                .setUv(0f, vOffset)
                .setColor(255, 255, 255, alpha)
                .setUv2(240, 240)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 0, -1);

        RenderSystem.disableBlend();
    }





    public void renderModel(PoseStack pPoseStack, MultiBufferSource pBufferSource, int p_289681_, ArmorItem pArmorItem, Model pModel, boolean r, float g, float b, float a, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        pModel.renderToBuffer(pPoseStack, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, 1);
    }







}
