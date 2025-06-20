package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.events.ForgeClientEvents;
import com.coderdan.avaritia.item.ModDataComponentTypes;
import com.coderdan.avaritia.item.armor.ArmorModelLayers;
import com.coderdan.avaritia.item.armor.ModInfinityArmorModel;
import com.coderdan.avaritia.item.custom.ModInfinityArmorItem;
import com.coderdan.avaritia.util.AnimatedMask;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.coderdan.avaritia.item.armor.ModInfinityArmorModel.createMeshes;


@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorRendererMixin <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private static ResourceLocation WINGTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing.png");
    private static ResourceLocation WINGTXTGLOW = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wingglow.png");
    private static ResourceLocation WINGTXTGLOW_TRIMMED = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wingglow_trimmed.png");
    private static ResourceLocation WINGTXT_TRIMMED = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing_trimmed.png");
    private static ResourceLocation WINGTXT_MASK = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask_wings.png");

    private static ResourceLocation VOIDTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask.png");
    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");
    private static ResourceLocation EMPTY_COLORTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/other/empty_color.png");
    private AnimatedMask wingMask;

    int colorIndex = 0; // put this as a class-level field or cache it elsewhere

    public ArmorRendererMixin(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }


    private static ModelPart flatPart;

    private static ModelPart getFlatPart() {
        if (flatPart == null) {
            flatPart = Minecraft.getInstance().getEntityModels().bakeLayer(ArmorModelLayers.INFINITY_ARMOR_FLAT);
        }
        return flatPart;
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


    boolean hasFullInfinityArmor(T pLivingEntity)
    {
        ItemStack helmetStack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestStack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack pantsStack = pLivingEntity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack bootsStack = pLivingEntity.getItemBySlot(EquipmentSlot.FEET);


        boolean helm = helmetStack.getItem() instanceof ModInfinityArmorItem;
        boolean chest = chestStack.getItem() instanceof ModInfinityArmorItem;
        boolean legs = pantsStack.getItem() instanceof ModInfinityArmorItem;
        boolean feet = bootsStack.getItem() instanceof ModInfinityArmorItem;

        return helm && chest && legs && feet;
    }

    boolean hasHeavenTrim(ItemStack result)
    {
        if (result.getItem() instanceof ArmorItem) {
            ArmorTrim trim = result.get(DataComponents.TRIM);
            if (trim != null && trim.pattern().isBound() && trim.material().isBound()) {
                String patternId = trim.pattern().getRegisteredName();
                String materialId = trim.material().get().ingredient().getRegisteredName();

                return patternId.equals("avaritia:heavens_mark") && materialId.equals("avaritia:infinity_ingot");
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    void updateUniformsHeaven()
    {
        if (ModRenderTypes.infinityVoidSolidShader != null) {
            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;

            Objects.requireNonNull(shader.getUniform("StarColorR"), "Missing Uniform 'StarColorR'!").set(1.0f);
            Objects.requireNonNull(shader.getUniform("StarColorG"), "Missing Uniform 'StarColorG'!").set(0.9f);
            Objects.requireNonNull(shader.getUniform("StarColorB"), "Missing Uniform 'StarColorB'!").set(0.6f);

            Objects.requireNonNull(shader.getUniform("BackgroundColorR"), "Missing Uniform 'BackgroundColorR'!").set(0.5f);
            Objects.requireNonNull(shader.getUniform("BackgroundColorG"), "Missing Uniform 'BackgroundColorG'!").set(0.42f);
            Objects.requireNonNull(shader.getUniform("BackgroundColorB"), "Missing Uniform 'BackgroundColorB'!").set(0.3f);
        }
    }


    private static void copyPose(ModelPart from, ModelPart to) {
        to.copyFrom(from);
    }


    float time = 0;
    float shaderZoom = 2f;


    @Inject(method = "renderArmorPiece", at = @At("TAIL"))
    private void onRender(PoseStack pPoseStack, MultiBufferSource pBufferSource, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci) throws IOException {


        ItemStack headStack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestStack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legStack = pLivingEntity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack bootsStack = pLivingEntity.getItemBySlot(EquipmentSlot.FEET);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        if (pSlot == EquipmentSlot.CHEST && (chestStack.getItem() instanceof ModInfinityArmorItem)) {


            if ((pLivingEntity instanceof Player player))
            {
                if (hasFullInfinityArmor((T) player)) {
                    if (player.getAbilities().flying || player.isFallFlying()) {


                        if(hasHeavenTrim(chestStack))
                        {
                            updateUniformsHeaven();
                        }
                        else
                        {
                            updateShaderUniformsArmor(0.2f, pPackedLight);
                        }

                        renderWings(pPoseStack, pModel, bufferSource, EquipmentSlot.CHEST, player, bufferSource::endBatch);


                    }

                }

            }


            float trimmedTransparency;

            if(hasHeavenTrim(chestStack))
            {
                updateUniformsHeaven();
                updateShaderTransparency(1f);
                trimmedTransparency = 1f;
            }
            else
            {
                updateShaderUniformsArmor(1f, pPackedLight);
                trimmedTransparency = 0.7f;

            }


            renderArmorEffects(pPoseStack, pModel, pBufferSource, pPackedLight, trimmedTransparency);


            if(hasHeavenTrim(chestStack))
            {
                updateUniformsHeaven();
            }



        }
        else if(pSlot == EquipmentSlot.HEAD && pLivingEntity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ModInfinityArmorItem)
        {


            ModelPart head = pModel.head;
            ModelPart body = pModel.body;
            ModelPart leftLeg = pModel.leftLeg;
            ModelPart rightLeg = pModel.rightLeg;
            ModelPart leftArm = pModel.leftArm;
            ModelPart rightArm = pModel.rightArm;


            // Eye color options
            float[][] COLORS = {
                    {1.0f, 0.41f, 0.71f}, // Pink
                    {0.0f, 1.0f, 0.0f},   // Lime Green
                    {0.0f, 1.0f, 1.0f},   // Cyan
                    {0.5f, 0.0f, 0.5f},   // Purple
                    {1.0f, 0.0f, 0.0f},   // Red
                    {1.0f, 0.65f, 0.0f},  // Orange
            };

            Minecraft mc = Minecraft.getInstance();
            long ticks = mc.level.getGameTime();

            if (ticks % 4 == 0) {
                colorIndex = (int)(Math.random() * COLORS.length);
            }

            float[] color = COLORS[colorIndex];


            float lsize = 0.125f;
            Vec3 loffset = new Vec3(0.15800011f, -0.3f, -0.3);

            float rsize = 0.125f;
            Vec3 roffset = new Vec3(-0.15800011f, -0.3f, -0.3);



            RenderType emptyRenderType = RenderType.beaconBeam(EMPTY_COLORTXT, false);
            RenderType voidRenderType =ModRenderTypes.infinityVoidTrueSolid(VOIDSTARSTXT);

            //more render stuff vv

            RenderType renderType = (hasFullInfinityArmor((T) pLivingEntity)) ? emptyRenderType : voidRenderType;

            VertexConsumer vc = pBufferSource.getBuffer(renderType);

            if((hasFullInfinityArmor((T) pLivingEntity))) {

                float[][] PULSE_COLORS = {
                        // Rising (10)
                        {0.50f, 0.42f, 0.30f},
                        {0.55f, 0.46f, 0.32f},
                        {0.60f, 0.50f, 0.34f},
                        {0.65f, 0.54f, 0.36f},
                        {0.70f, 0.58f, 0.38f},
                        {0.75f, 0.62f, 0.40f},
                        {0.80f, 0.66f, 0.42f},
                        {0.85f, 0.70f, 0.45f},
                        {0.90f, 0.74f, 0.48f},
                        {0.95f, 0.78f, 0.51f},

                        // Smoother transition into bright yellow (5)
                        {0.96f, 0.82f, 0.58f},
                        {0.97f, 0.87f, 0.65f},
                        {0.98f, 0.92f, 0.73f},
                        {0.99f, 0.96f, 0.80f},
                        {253 / 255f, 255 / 255f, 216 / 255f}, // ~0.99, 1.0, 0.85

                        // Hold peak (20)
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},
                        {253 / 255f, 255 / 255f, 216 / 255f},

                        // Smooth fade out (5)
                        {0.99f, 0.96f, 0.80f},
                        {0.98f, 0.92f, 0.73f},
                        {0.97f, 0.87f, 0.65f},
                        {0.96f, 0.82f, 0.58f},
                        {0.95f, 0.78f, 0.51f},

                        // Falling (10)
                        {0.90f, 0.74f, 0.48f},
                        {0.85f, 0.70f, 0.45f},
                        {0.80f, 0.66f, 0.42f},
                        {0.75f, 0.62f, 0.40f},
                        {0.70f, 0.58f, 0.38f},
                        {0.65f, 0.54f, 0.36f},
                        {0.60f, 0.50f, 0.34f},
                        {0.55f, 0.46f, 0.32f},
                        {0.50f, 0.42f, 0.30f}
                };










                float[] finalColors;

                if (hasHeavenTrim(headStack)) {
                    int frameCount = PULSE_COLORS.length;

                    // Cycle with sine
                    float cycle = (float) Math.sin(ticks * 0.025f) * 0.5f + 0.5f; // range 0-1
                    int index = Math.round(cycle * (frameCount - 1));
                    finalColors = PULSE_COLORS[index];
                } else {
                    finalColors = color;
                }

                // Render LEFT eye
                pPoseStack.pushPose();

                head.translateAndRotate(pPoseStack); // attach to body
                pPoseStack.translate(loffset.x, loffset.y, loffset.z); // right side
                renderColoredReversedWingQuad(pPoseStack, vc, lsize, finalColors[0], finalColors[1], finalColors[2]);
                pPoseStack.popPose();

                // Render RIGHT eye
                pPoseStack.pushPose();

                head.translateAndRotate(pPoseStack); // attach to body
                pPoseStack.translate(roffset.x, roffset.y, roffset.z); // right side
                renderColoredReversedWingQuad(pPoseStack, vc, rsize, finalColors[0], finalColors[1], finalColors[2]);
                pPoseStack.popPose();
            }
            else
            {
                // Render LEFT eye
                pPoseStack.pushPose();

                head.translateAndRotate(pPoseStack); // attach to body
                pPoseStack.translate(loffset.x, loffset.y, loffset.z); // right side
                renderReversedWingQuad(pPoseStack, vc, lsize);
                pPoseStack.popPose();

                // Render RIGHT eye
                pPoseStack.pushPose();

                head.translateAndRotate(pPoseStack); // attach to body
                pPoseStack.translate(roffset.x, roffset.y, roffset.z); // right side
                renderReversedWingQuad(pPoseStack, vc, rsize);
                pPoseStack.popPose();
            }

            VertexConsumer Maskvc = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));

            updateShaderTransparency(1f);
            float pixelTransparency;
            if(hasHeavenTrim(headStack))
            {
                updateUniformsHeaven();
                pixelTransparency = 1f;
            }
            else
            {
                updateShaderUniformsArmor(1f, pPackedLight);
                pixelTransparency = 0.3f;
            }

            // Head
            pPoseStack.pushPose();
            head.translateAndRotate(pPoseStack);
            Vec3 headRightOffset = new Vec3(-0.1172f, -0.0937f, -0.3141f);
            renderFaceStripReversed(pPoseStack, Maskvc, 0.0389f, 0.0391f, 2, headRightOffset, 34, 22, 4, 7, 64, 64, pixelTransparency);
            pPoseStack.popPose();


            if(!hasHeavenTrim(headStack))
            {
                pixelTransparency = 0.65f;
            }


            pPoseStack.pushPose();
            head.translateAndRotate(pPoseStack);
            Vec3 headRightlowerOffset = new Vec3(-0.1172f, -0.0937f + 0.0782f, -0.3141f);
            renderFaceStripReversed(pPoseStack, Maskvc, 0.0389f, 0.0391f, 2, headRightlowerOffset, 34, 22, 4, 7, 64, 64, pixelTransparency);
            pPoseStack.popPose();

            if(!hasHeavenTrim(headStack))
            {
                pixelTransparency = 0.3f;
            }


            pPoseStack.pushPose();
            head.translateAndRotate(pPoseStack);
            Vec3 headLeftOffset = new Vec3(-0.1172f + (0.0782f * 3), -0.0937f, -0.3141f);
            renderFaceStripReversed(pPoseStack, Maskvc, 0.0389f, 0.0391f, 2, headLeftOffset, 34, 22, 4, 7, 64, 64, pixelTransparency);
            pPoseStack.popPose();

            if(!hasHeavenTrim(headStack))
            {
                pixelTransparency = 0.65f;
            }


            pPoseStack.pushPose();
            head.translateAndRotate(pPoseStack);
            Vec3 headLeftlowerOffset = new Vec3(-0.1172f  + (0.0782f * 3), -0.0937f + 0.0782f, -0.3141f);
            renderFaceStripReversed(pPoseStack, Maskvc, 0.0389f, 0.0391f, 2, headLeftlowerOffset, 34, 22, 4, 7, 64, 64, pixelTransparency);
            pPoseStack.popPose();

            bufferSource.endBatch();


        } else if(pSlot == EquipmentSlot.LEGS && pLivingEntity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ModInfinityArmorItem)
        {



            ModelPart head = pModel.head;
            ModelPart body = pModel.body;
            ModelPart leftLeg = pModel.leftLeg;
            ModelPart rightLeg = pModel.rightLeg;
            ModelPart leftArm = pModel.leftArm;
            ModelPart rightArm = pModel.rightArm;

            Vec3 legOffset = new Vec3(0f, 0.1724f, -0.159);

            VertexConsumer vc = pBufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));

            float trimmedTransparency;

            if(hasHeavenTrim(legStack))
            {
                updateUniformsHeaven();
                updateShaderTransparency(1f);
                trimmedTransparency = 1f;
            }
            else
            {
                updateShaderUniformsArmor(1f, pPackedLight);
                trimmedTransparency = 0.7f;

            }

            pPoseStack.pushPose();
            leftLeg.translateAndRotate(pPoseStack);
            renderQuadStripReversed(pPoseStack, vc, 0.078f, 0.1350f, 1, legOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);
            pPoseStack.popPose();

            pPoseStack.pushPose();
            rightLeg.translateAndRotate(pPoseStack);
            renderQuadStripReversed(pPoseStack, vc, 0.078f, 0.1350f, 1, legOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);
            pPoseStack.popPose();

        }
        else if(pSlot == EquipmentSlot.FEET && pLivingEntity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ModInfinityArmorItem)
        {
            if(hasHeavenTrim(bootsStack))
            {
                updateUniformsHeaven();
            }
        }




        if(hasFullInfinityArmor(pLivingEntity))
        {
            //render base armor shader effects
            renderArmorShaderBase(pModel, pPoseStack, pPackedLight);

            if(hasHeavenTrim(headStack) && hasHeavenTrim(chestStack) && hasHeavenTrim(legStack) && hasHeavenTrim(bootsStack))
            {
                updateUniformsHeaven();
            }
            else
            {
                updateShaderUniformsArmor(0.2f, pPackedLight);
            }
        }

    }

    void updateShaderUniforms(float PixelValue, int pPackedLight)
    {

        if (ModRenderTypes.infinityVoidSolidShader != null) {
            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;

            float partialTicks = Minecraft.getInstance().getFrameTimeNs();
            float gameTime = Minecraft.getInstance().level.getGameTime() + partialTicks;

            int frameCount = 8;
            int frameDuration = 75; // ms per frame
            int currentFrame = (int)((System.currentTimeMillis() / frameDuration) % frameCount);


            boolean isInventory = Minecraft.getInstance().screen != null;
            boolean isMaxLight = pPackedLight == 15728880;

            boolean isHUD = !isInventory && isMaxLight;
            boolean isTrulyInventory = isInventory && isMaxLight;

            shaderZoom = isTrulyInventory ? 25f :
                    isHUD             ? 25f :
                            Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 3f : 5f;

            Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);


            if (isTrulyInventory || isHUD) {
                float yaw = 0;
                float pitch = 0;

                Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
            }
            else
            {
                float yaw = Minecraft.getInstance().player.getYRot();
                float pitch = Minecraft.getInstance().player.getXRot();

                Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
            }



            Objects.requireNonNull(shader.getUniform("GameTime")).set(gameTime);
            Objects.requireNonNull(shader.getUniform("currentFrame")).set((int) currentFrame);
            Objects.requireNonNull(shader.getUniform("frameCount")).set((int) frameCount);
            Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);
            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) PixelValue);


            Uniform starColorRUniform = Objects.requireNonNull(shader.getUniform("StarColorR"), "Missing Uniform 'StarColorR'!");
            Uniform starColorGUniform = Objects.requireNonNull(shader.getUniform("StarColorG"), "Missing Uniform 'StarColorG'!");
            Uniform starColorBUniform = Objects.requireNonNull(shader.getUniform("StarColorB"), "Missing Uniform 'StarColorB'!");

            starColorRUniform.set(0.7f);
            starColorGUniform.set(1.0f);
            starColorBUniform.set(1.0f);

            Uniform BackgroundColorRUniform = Objects.requireNonNull(shader.getUniform("BackgroundColorR"), "Missing Uniform 'BackgroundColorR'!");
            Uniform BackgroundColorGUniform = Objects.requireNonNull(shader.getUniform("BackgroundColorG"), "Missing Uniform 'BackgroundColorG'!");
            Uniform BackgroundColorBUniform = Objects.requireNonNull(shader.getUniform("BackgroundColorB"), "Missing Uniform 'BackgroundColorB'!");


            BackgroundColorRUniform.set(0.1f);
            BackgroundColorGUniform.set(0.225f);
            BackgroundColorBUniform.set(0.3f);
        }
    }




    void updateShaderUniformsArmor(float PixelValue, int pPackedLight)
    {

        if (ModRenderTypes.infinityVoidSolidShader != null) {
            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;

            float partialTicks = Minecraft.getInstance().getFrameTimeNs();
            float gameTime = Minecraft.getInstance().level.getGameTime() + partialTicks;

            int frameCount = 8;
            int frameDuration = 75; // ms per frame
            int currentFrame = (int)((System.currentTimeMillis() / frameDuration) % frameCount);


            boolean isInventory = Minecraft.getInstance().screen != null;
            boolean isMaxLight = pPackedLight == 15728880;

            boolean isHUD = !isInventory && isMaxLight;
            boolean isTrulyInventory = isInventory && isMaxLight;

            shaderZoom = isTrulyInventory ? 25f :
                    isHUD             ? 25f :
                            Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 3f : 5f;

            Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);


            if (isTrulyInventory || isHUD) {
                float yaw = 0;
                float pitch = 0;

                Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
            }
            else
            {
                float yaw = Minecraft.getInstance().player.getYRot();
                float pitch = Minecraft.getInstance().player.getXRot();

                Objects.requireNonNull(shader.getUniform("CameraYaw")).set(yaw);
                Objects.requireNonNull(shader.getUniform("CameraPitch")).set(pitch);
            }



            Objects.requireNonNull(shader.getUniform("GameTime")).set(gameTime);
            Objects.requireNonNull(shader.getUniform("currentFrame")).set((int) currentFrame);
            Objects.requireNonNull(shader.getUniform("frameCount")).set((int) frameCount);
            Objects.requireNonNull(shader.getUniform("ZoomScale")).set((float) shaderZoom);
            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) PixelValue);
        }
    }

    void updateShaderTransparency(float t)
    {

        if (ModRenderTypes.infinityVoidSolidShader != null) {
            ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;
            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) t);
        }
    }


    void renderArmorShaderBase(A pModel, PoseStack pPoseStack, int pPackedLight)
    {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        bufferSource.endBatch();

        VertexConsumer shaderBuffer = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));

        ModelPart left = getFlatPart().getChild("left_arm");
        ModelPart right = getFlatPart().getChild("right_arm");
        ModelPart body = getFlatPart().getChild("body");
        ModelPart head = getFlatPart().getChild("head");
        ModelPart legL = getFlatPart().getChild("left_leg");
        ModelPart legR = getFlatPart().getChild("right_leg");


        copyPose(pModel.leftArm, left);
        copyPose(pModel.rightArm, right);
        copyPose(pModel.body, body);
        copyPose(pModel.head, head);
        copyPose(pModel.leftLeg, legL);
        copyPose(pModel.rightLeg, legR);

        left.render(pPoseStack, shaderBuffer, pPackedLight, 1);
        right.render(pPoseStack, shaderBuffer, pPackedLight, 1);
        body.render(pPoseStack, shaderBuffer, pPackedLight, 1);
        head.render(pPoseStack, shaderBuffer, pPackedLight, 1);
        legL.render(pPoseStack, shaderBuffer, pPackedLight, 1);
        legR.render(pPoseStack, shaderBuffer, pPackedLight, 1);
    }




    void renderWings(PoseStack poseStack, A model, MultiBufferSource bufferSource, EquipmentSlot slot, LivingEntity player, @Nullable Runnable flush) throws IOException {

        Boolean trimmed = hasHeavenTrim(player.getItemBySlot(EquipmentSlot.CHEST));

        ResourceLocation WING_TEXTURE = trimmed ? WINGTXT_TRIMMED : WINGTXT;
        ResourceLocation WING_TEXTURE_GLOW = trimmed ? WINGTXTGLOW_TRIMMED : WINGTXTGLOW;


        if (ModRenderTypes.babyShader(WING_TEXTURE) == null) return;

        time += 0.05f;
        Objects.requireNonNull(ModRenderTypes.babyShader.getUniform("time")).set(time);

        ModelPart body = model.body;

        float wingOffset = (float) -0.0300f;
        float yWingOffset = (float) -0.7300f;
        float zWingOffset = (float) 0.1200f;

        float size = 2.0100f;
        float wingSpreadDegrees = 20f;

        // Load the animated mask
        if (wingMask == null) {
            try {
                wingMask = new AnimatedMask(WINGTXT_MASK);
            } catch (IOException e) {
                System.err.println("Failed to load animated mask: " + WINGTXT_MASK);
                return;
            }
        }


        // PASS 1
        VertexConsumer vcWingLeft = bufferSource.getBuffer(ModRenderTypes.babyShader(WING_TEXTURE));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(-wingOffset, yWingOffset, zWingOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees));
        renderWingQuad(poseStack, vcWingLeft, size);
        poseStack.popPose();

        flush.run();

        VertexConsumer vcWingLeftPulse =  bufferSource.getBuffer(RenderType.entityTranslucentEmissive(WING_TEXTURE_GLOW));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(-wingOffset, yWingOffset, zWingOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees));
        renderWingQuadPulsate(poseStack, vcWingLeftPulse, size);
        poseStack.popPose();

        flush.run();


        // PASS 2
        VertexConsumer vcMaskLeft = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(-wingOffset, yWingOffset, zWingOffset + 0.001f);
        poseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees));
        renderWingQuadFromAnimatedMaskWithShader(poseStack, vcMaskLeft, size, wingMask, flush, false);
        poseStack.popPose();

        flush.run();

        VertexConsumer vcMaskLeftFront = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(-wingOffset, yWingOffset, zWingOffset - 0.001f);
        poseStack.mulPose(Axis.YP.rotationDegrees(wingSpreadDegrees));
        renderWingQuadFromAnimatedMaskWithShaderReversed(poseStack, vcMaskLeftFront, size, wingMask, flush, false);
        poseStack.popPose();

        flush.run();

        // === RIGHT WING ===

        // PASS 1
        VertexConsumer vcWingRight = bufferSource.getBuffer(ModRenderTypes.babyShader(WING_TEXTURE));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(wingOffset, yWingOffset, zWingOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees));
        poseStack.scale(-1.0f, 1.0f, 1.0f); // mirror X
        renderWingQuad(poseStack, vcWingRight, size);
        poseStack.popPose();

        flush.run();

        VertexConsumer vcWingRightPulse =  bufferSource.getBuffer(RenderType.entityTranslucentEmissive(WING_TEXTURE_GLOW));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(wingOffset, yWingOffset, zWingOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees));
        poseStack.scale(-1.0f, 1.0f, 1.0f); // mirror X
        renderWingQuadPulsate(poseStack, vcWingRightPulse, size);
        poseStack.popPose();

        flush.run();

        // PASS 2
        VertexConsumer vcMaskRight = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(wingOffset, yWingOffset, zWingOffset + 0.001f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees));
        poseStack.scale(-1.0f, 1.0f, 1.0f);
        renderWingQuadFromAnimatedMaskWithShaderReversed(poseStack, vcMaskRight, size, wingMask, flush, false);
        poseStack.popPose();

        flush.run();

        VertexConsumer vcMaskRightFront = bufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));
        poseStack.pushPose();
        body.translateAndRotate(poseStack);
        poseStack.translate(wingOffset, yWingOffset, zWingOffset - 0.001f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-wingSpreadDegrees));
        poseStack.scale(-1.0f, 1.0f, 1.0f);
        renderWingQuadFromAnimatedMaskWithShader(poseStack, vcMaskRightFront, size, wingMask, flush, false);
        poseStack.popPose();

        flush.run();



    }




    void renderArmorEffects(PoseStack pPoseStack, A pModel, MultiBufferSource pBufferSource, int light, float trimmedTransparency)
    {
        VertexConsumer vc = pBufferSource.getBuffer(ModRenderTypes.infinityVoidSolidShader(VOIDSTARSTXT));




        ModelPart head = pModel.head;
        ModelPart body = pModel.body;
        ModelPart leftLeg = pModel.leftLeg;
        ModelPart rightLeg = pModel.rightLeg;
        ModelPart leftArm = pModel.leftArm;
        ModelPart rightArm = pModel.rightArm;


        pPoseStack.pushPose();
        body.translateAndRotate(pPoseStack);

        updateShaderUniforms(1f, light);

        float dsize = 0.0362f;
        Vec3 doffset = new Vec3(0.1170f, 0.1204f, 0.189f);

        renderQuadStripTransparent(pPoseStack, vc, dsize, 7, doffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);
        float spacing = dsize * 2.14f * 3.0f;
        Vec3 nextOffset = doffset.add(-spacing, 0.0f, 0.0f);
        renderQuadStripTransparent(pPoseStack, vc, dsize, 7, nextOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);

        Vec3 centerOffset = new Vec3(0.0000, 0.2294, -0.1890);
        renderQuadStripReversedTransparent(pPoseStack, vc, 0.08f, 0.1450f, 1, centerOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);

        // Left Arm
        pPoseStack.pushPose();
        leftArm.translateAndRotate(pPoseStack);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
        Vec3 leftArmOffset = new Vec3(0.0000, -0.1146, 0.2510);
        renderQuadStrip(pPoseStack, vc, 0.0930f, 0.1450f, 1, leftArmOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);
        pPoseStack.popPose();

        // Right Arm
        pPoseStack.pushPose();
        rightArm.translateAndRotate(pPoseStack);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
        Vec3 rightArmOffset = new Vec3(0.0000, -0.1146, -0.2510);
        renderQuadStripReversed(pPoseStack, vc, 0.0930f, 0.1450f, 1, rightArmOffset, 34, 22, 4, 7, 64, 64, trimmedTransparency);
        pPoseStack.popPose();




        pPoseStack.popPose();






    }



    private void renderWingQuad(PoseStack pPoseStack, VertexConsumer vc, float size) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;

                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
            }
        }

        RenderSystem.disableBlend();
    }

    private void renderWingQuadPulsate(PoseStack pPoseStack, VertexConsumer vc, float size) {
        Matrix4f matrix = pPoseStack.last().pose();
        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;

        // Pulse alpha using a sine wave over 3 seconds
        double time = (System.currentTimeMillis() % 3000) / 3000.0; // 0.0 to 1.0
        float alpha = (float)((Math.sin(time * 2 * Math.PI) + 1) / 2); // 0 to 1
        int alpha255 = Math.round(alpha * 255);

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;

                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, alpha255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, alpha255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, alpha255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, alpha255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
            }
        }

        RenderSystem.disableBlend();
    }

    private void renderWingQuadFromAnimatedMask(PoseStack pPoseStack, VertexConsumer vc, float size, AnimatedMask mask, @Nullable Runnable flush, boolean invert) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;
        NativeImage frame = mask.getCurrentFrameFromHeight(texSize);
        boolean drewAnything = false;

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                int color = frame.getPixelRGBA(x, y);
                int a = (color >> 24) & 0xFF;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                float brightness = (r + g + b) * 2 / (3f * 255f); // normalize to 0–1
                boolean shouldRender = invert ? brightness <= 0.1f : brightness > 0.1f;
                if (!shouldRender) continue;

                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;

                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255)
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255)
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255)
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255)
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

                drewAnything = true;
            }
        }

        if (flush != null && drewAnything) {
            flush.run();
        }

        RenderSystem.disableBlend();
    }


    private void renderWingQuadFromAnimatedMaskWithShader(PoseStack pPoseStack, VertexConsumer vc, float size, AnimatedMask mask, @Nullable Runnable flush, boolean invert) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;
        NativeImage frame = mask.getCurrentFrameFromHeight(texSize);
        boolean drewAnything = false;

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                int color = frame.getPixelRGBA(x, y);
                int a = (color >> 24) & 0xFF;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                float brightness = (r + g + b) / (3f * 255f); // normalize to 0–1
                boolean shouldRender = invert ? brightness <= 0.01f : brightness > 0.01f;
                if(!shouldRender) continue;

                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;

                float boosted = Mth.clamp(brightness, 0f, 1f);

                if (ModRenderTypes.infinityVoidSolidShader != null) {
                    ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;

                    Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) 1);
                }

                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

                drewAnything = true;
            }
        }

        if (flush != null && drewAnything) {
            flush.run();
        }

        RenderSystem.disableBlend();
    }


    private void renderWingQuadFromAnimatedMaskWithShaderReversed(PoseStack pPoseStack, VertexConsumer vc, float size, AnimatedMask mask, @Nullable Runnable flush, boolean invert) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;
        NativeImage frame = mask.getCurrentFrameFromHeight(texSize);
        boolean drewAnything = false;

        List<String> pixelLog = new ArrayList<>();

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                int color = frame.getPixelRGBA(x, y);
                int a = (color >> 24) & 0xFF;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                float brightness = (r + g + b) / (3f * 255f); // normalize to 0–1
                boolean shouldRender = invert ? brightness <= 0.01f : brightness > 0.01f;
                if(!shouldRender) continue;


                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;


                float quadBrightness = Mth.clamp(brightness * 255f * 1.5f, 0f, 255f);
                float boosted = Mth.clamp(brightness, 0f, 1f);

                if (ModRenderTypes.infinityVoidSolidShader != null) {
                    ShaderInstance shader = ModRenderTypes.infinityVoidSolidShader;

                    Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) 1);
                }

                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, Math.round(boosted * 255))
                        .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);


                drewAnything = true;
            }
        }

        if (flush != null && drewAnything) {
            flush.run();
        }

        if (!pixelLog.isEmpty()) {
            System.out.println("Non-zero pixels:");
            System.out.println(String.join(", ", pixelLog));
        }

        if (flush != null && drewAnything) {
            flush.run();
        }

        RenderSystem.disableBlend();
    }




    private void renderWingAnimatedQuad(PoseStack pPoseStack, VertexConsumer vc, float size, int frameHeight, int imageWidth, int imageHeight, int frameDurationTicks) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;

        // Get the current frame index based on system time
        long ticks = System.currentTimeMillis() / 50; // 20 ticks per second
        int totalFrames = imageHeight / frameHeight;
        int currentFrame = (int) (ticks / frameDurationTicks) % totalFrames;

        float frameVStart = (frameHeight * currentFrame) / (float) imageHeight;
        float frameVEnd = (frameHeight * (currentFrame + 1)) / (float) imageHeight;

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / (float) imageWidth;
                float u1 = (x + 1) / (float) imageWidth;

                float v0 = frameVStart + (y / (float) texSize) * (frameVEnd - frameVStart);
                float v1 = frameVStart + ((y + 1) / (float) texSize) * (frameVEnd - frameVStart);

                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
            }
        }

        RenderSystem.disableBlend();
    }



    private void renderReversedWingQuad(PoseStack pPoseStack, VertexConsumer vc, float size) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();

        int texSize = 64;
        float pixelSize = (2f * size) / texSize;

        for (int y = 0; y < texSize; y++) {
            for (int x = 0; x < texSize; x++) {
                float x0 = -size + x * pixelSize;
                float y0 = -size + y * pixelSize;
                float x1 = x0 + pixelSize;
                float y1 = y0 + pixelSize;

                float u0 = x / 64f;
                float v0 = y / 64f;
                float u1 = (x + 1) / 64f;
                float v1 = (y + 1) / 64f;

                vc.addVertex(matrix, x0, y1, 0.0f).setUv(u0, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y1, 0.0f).setUv(u1, v0).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x1, y0, 0.0f).setUv(u1, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
                vc.addVertex(matrix, x0, y0, 0.0f).setUv(u0, v1).setColor(255, 255, 255, 255).setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
            }
        }

        RenderSystem.disableBlend();

    }

    private void renderColoredReversedWingQuad(PoseStack pPoseStack, VertexConsumer vc, float size, float r, float g, float b) {
        Matrix4f matrix = pPoseStack.last().pose();

        RenderSystem.enableBlend();


        vc.addVertex(matrix, -size, size, 0.0f)
                .setUv(0.0f, 0.0f).setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, size, 0.0f)
                .setUv(1.0f, 0.0f).setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, size, -size, 0.0f)
                .setUv(1.0f, 1.0f).setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), 255)
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -size, -size, 0.0f)
                .setUv(0.0f, 1.0f).setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), 255)
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

    private void renderQuadStripTransparent(PoseStack poseStack, VertexConsumer vc, float size, int length, Vec3 offset,
                                 float u, float v, float regionWidth, float regionHeight,
                                 float textureWidth, float textureHeight, float pixelValue) {
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
                .setUv(minU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, -size + totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, -size + totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);


        RenderSystem.disableBlend();
        poseStack.popPose();
    }


    private void renderQuadStrip(PoseStack poseStack, VertexConsumer vc,
                                 float width, float heightPerSegment, int length, Vec3 offset,
                                 float u, float v, float regionWidth, float regionHeight,
                                 float textureWidth, float textureHeight, float pixelValue) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = heightPerSegment * length;

        vc.addVertex(matrix, -width, 0.0f, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, 0.0f, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderQuadStripReversed(PoseStack poseStack, VertexConsumer vc,
                                 float width, float heightPerSegment, int length, Vec3 offset,
                                 float u, float v, float regionWidth, float regionHeight,
                                 float textureWidth, float textureHeight, float pixelValue) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = heightPerSegment * length;

        vc.addVertex(matrix, -width, totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, 0.0f, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, 0.0f, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);



        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderQuadStripReversedTransparent(PoseStack poseStack, VertexConsumer vc,
                                         float width, float heightPerSegment, int length, Vec3 offset,
                                         float u, float v, float regionWidth, float regionHeight,
                                         float textureWidth, float textureHeight, float pixelValue) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = heightPerSegment * length;

        vc.addVertex(matrix, -width, totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, 0.0f, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, 0.0f, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, Math.round(pixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);



        RenderSystem.disableBlend();
        poseStack.popPose();
    }



    private void renderFaceStripReversed(PoseStack poseStack, VertexConsumer vc,
                                         float width, float heightPerSegment, int length, Vec3 offset,
                                         float u, float v, float regionWidth, float regionHeight,
                                         float textureWidth, float textureHeight, float PixelValue) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();

        float minU = u / textureWidth;
        float maxU = (u + regionWidth) / textureWidth;
        float minV = v / textureHeight;
        float maxV = (v + regionHeight * length) / textureHeight;

        float totalHeight = heightPerSegment * length;

        vc.addVertex(matrix, -width, totalHeight, 0.0f)
                .setUv(minU, minV).setColor(255, 255, 255, Math.round(PixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, totalHeight, 0.0f)
                .setUv(maxU, minV).setColor(255, 255, 255, Math.round(PixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, width, 0.0f, 0.0f)
                .setUv(maxU, maxV).setColor(255, 255, 255, Math.round(PixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);
        vc.addVertex(matrix, -width, 0.0f, 0.0f)
                .setUv(minU, maxV).setColor(255, 255, 255, Math.round(PixelValue * 255))
                .setUv2(240, 240).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0, 0, 1);



        RenderSystem.disableBlend();
        poseStack.popPose();
    }













}
