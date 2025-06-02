package com.coderdan.avaritia.item.armor;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;


public class ModInfinityArmorModel extends HumanoidModel<LivingEntity> {

    private static ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");


    public ModInfinityArmorModel(ModelPart root, EquipmentSlot slot) {
        super(root);
    }

    public static LayerDefinition createBodyLayer(Boolean islegs) {
        // Pass false or true depending on whether it's for leggings or not
        MeshDefinition mesh = createMeshes(new CubeDeformation(1f), 0f, islegs);
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createBodyLayerFlat() {
        MeshDefinition mesh = createMeshes(new CubeDeformation(0.1f), 0f, false);
        return LayerDefinition.create(mesh, 64, 32);
    }


    private LivingEntity entity;

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }


    public LivingEntity getEntity() {
        return this.entity;
    }

    float time = 0;
    float shaderZoom = 2f;

    // override
    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);

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
            Objects.requireNonNull(shader.getUniform("PixelAlpha")).set((float) 0.2f);
        }


    }


    //creates vanilla base armor layer
    public static MeshDefinition createMeshes(CubeDeformation c, float f, boolean islegs) {
        int heightoffset = 0;
        int legoffset = islegs ? 32 : 0;
        MeshDefinition m = new MeshDefinition();
        PartDefinition p = m.getRoot();
        p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, c), PartPose.offset(0.0F, 0.0F + f, 0.0F));
        p.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, c), PartPose.offset(0.0F, 0.0F + f, 0.0F));
        p.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, c), PartPose.offset(-5.0F, 2.0F + f, 0.0F));
        p.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, c), PartPose.offset(5.0F, 2.0F + f, 0.0F));
        p.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, c), PartPose.offset(-1.9F, 12.0F + f, 0.0F));
        p.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, c), PartPose.offset(1.9F, 12.0F + f, 0.0F));
        if (islegs) {
            p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16 + legoffset).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, (0 + heightoffset), 0.0F));
            p.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16 + legoffset).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, (12 + heightoffset), 0.0F));
            p.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16 + legoffset).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, (12 + heightoffset), 0.0F));
        }
        return m;
    }









}


