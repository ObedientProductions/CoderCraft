package com.coderdan.avaritia;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Objects;

public class ModRenderTypes {

    private static ResourceLocation TXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing.png");



    public static ShaderInstance babyShader;
    public static ShaderInstance infinityVoidShader;
    public static ShaderInstance infinityVoidSolidShader;
    public static ShaderInstance infinityVoidWings;

    public static RenderType babyShader(ResourceLocation txt) {

        return CustomRenderTypes.babyTranslucent(txt);
    }

    public static RenderType infinityVoidShader(ResourceLocation txt) {

        return CustomRenderTypes.infinity_void(txt);
    }

    public static RenderType infinityVoidSolidShader(ResourceLocation txt) {

        return CustomRenderTypes.infinity_void_solid(txt);
    }

    public static RenderType modGui(ResourceLocation txt) {

        return CustomRenderTypes.modGuiTextured(txt);
    }

    public static RenderType infinityVoidTrueSolid(ResourceLocation txt) {
        return CustomRenderTypes.infinity_void_fully_solid(txt);
    }

    public static RenderType infinityVoidWings(ResourceLocation txt) {
        return CustomRenderTypes.infinity_void_wings(txt);
    }

    public static RenderType cutoutGlow(ResourceLocation txt) {
        return CustomRenderTypes.glowCutout(txt);
    }




    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":babyshader"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.babyShader = shader;
                ModRenderTypes.babyShader = shader;
                setDefaultUniformTime(shader);

                System.out.println("Baby shader registered!");
            });

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":infinity_void"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.infinityVoidShader = shader;
                ModRenderTypes.infinityVoidShader = shader;
                setDefaultUniformsInfinityVoid(shader);

                System.out.println("Infinity void shader registered!");
            });

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":infinity_void_solid"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.infinityVoidSolidShader = shader;
                ModRenderTypes.infinityVoidSolidShader = shader;
                setDefaultUniformsInfinityVoid(shader);


                System.out.println("Infinity void solid shader registered!");
            });

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":infinity_void_solid"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.infinityVoidFullySolidShader = shader;
                setDefaultUniformsInfinityVoid(shader);

                System.out.println("Infinity void fully solid shader registered!");
            });


            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":infinity_void"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.infinityVoidWingsShader = shader;
                setDefaultUniformsInfinityVoid(shader);

                System.out.println("Infinity void wings shader registered!");
            });

        }
    }

    private static void setDefaultUniformTime(ShaderInstance shader) {

        // Check that the shader is not null
        if (shader == null) {
            System.err.println("Shader is null!");
            return;
        }

        // Get matrices and window
        Window win = Minecraft.getInstance().getWindow();
        Matrix4f frustumMatrix = RenderSystem.getProjectionMatrix();
        Matrix4f projectionMatrix = RenderSystem.getModelViewMatrix();

        // Set shader defaults
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, frustumMatrix, projectionMatrix, win);

        // Set the time uniform in the shader
        Uniform timeUniform = Objects.requireNonNull(shader.getUniform("time"), "Shader uniform 'time' not found!");
        timeUniform.set(0.0f);



        System.out.println("Default uniform 'time' set!");
    }

    private static void setDefaultUniformsInfinityVoid(ShaderInstance shader) {

        // Check that the shader is not null
        if (shader == null) {
            System.err.println("Shader is null!");
            return;
        }

        // Get matrices and window
        Window win = Minecraft.getInstance().getWindow();
        Matrix4f frustumMatrix = RenderSystem.getProjectionMatrix();
        Matrix4f projectionMatrix = RenderSystem.getModelViewMatrix();

        // Set shader defaults
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, frustumMatrix, projectionMatrix, win);

        // Set ModelViewMat
        Uniform modelViewMat = Objects.requireNonNull(shader.getUniform("ModelViewMat"), "Missing Uniform 'ModelViewMat'!");
        modelViewMat.set(frustumMatrix);

        // Set ProjMat
        Uniform projMat = Objects.requireNonNull(shader.getUniform("ProjMat"), "Missing Uniform 'ProjMat'!");
        projMat.set(projectionMatrix);

        // Set the time uniform in the shader
        Uniform timeUniform = Objects.requireNonNull(shader.getUniform("GameTime"), "Missing Uniform 'GameTime'!");
        timeUniform.set(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());

        // Set the time uniform in the shader
        Uniform frameCountUniform = Objects.requireNonNull(shader.getUniform("frameCount"), "Missing Uniform 'frameCount'!");
        frameCountUniform.set(8);

        // Set the time uniform in the shader
        Uniform currentFrameUniform = Objects.requireNonNull(shader.getUniform("currentFrame"), "Missing Uniform 'currentFrame'!");
        currentFrameUniform.set(0);

        // Set the time uniform in the shader
        Uniform zoomScaleUniform = Objects.requireNonNull(shader.getUniform("ZoomScale"), "Missing Uniform 'currentFrame'!");
        zoomScaleUniform.set(1.0f);

        // Set the time uniform in the shader
        Uniform cameraYawUniform = Objects.requireNonNull(shader.getUniform("CameraYaw"), "Missing Uniform 'currentFrame'!");
        cameraYawUniform.set(0f);

        // Set the time uniform in the shader
        Uniform cameraPitchUniform = Objects.requireNonNull(shader.getUniform("CameraPitch"), "Missing Uniform 'currentFrame'!");
        cameraPitchUniform.set(0f);



        System.out.println("Default uniform 'time' set!");
    }


    private static void setDefaultUniformsInfinityVoidSolid(ShaderInstance shader) {

        // Check that the shader is not null
        if (shader == null) {
            System.err.println("Shader is null!");
            return;
        }

        // Get matrices and window
        Window win = Minecraft.getInstance().getWindow();
        Matrix4f frustumMatrix = RenderSystem.getProjectionMatrix();
        Matrix4f projectionMatrix = RenderSystem.getModelViewMatrix();

        // Set shader defaults
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, frustumMatrix, projectionMatrix, win);

        // Set ModelViewMat
        Uniform modelViewMat = Objects.requireNonNull(shader.getUniform("ModelViewMat"), "Missing Uniform 'ModelViewMat'!");
        modelViewMat.set(frustumMatrix);

        // Set ProjMat
        Uniform projMat = Objects.requireNonNull(shader.getUniform("ProjMat"), "Missing Uniform 'ProjMat'!");
        projMat.set(projectionMatrix);

        // Set the time uniform in the shader
        Uniform timeUniform = Objects.requireNonNull(shader.getUniform("GameTime"), "Missing Uniform 'GameTime'!");
        timeUniform.set(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());

        // Set the time uniform in the shader
        Uniform frameCountUniform = Objects.requireNonNull(shader.getUniform("frameCount"), "Missing Uniform 'frameCount'!");
        frameCountUniform.set(8);

        // Set the time uniform in the shader
        Uniform currentFrameUniform = Objects.requireNonNull(shader.getUniform("currentFrame"), "Missing Uniform 'currentFrame'!");
        currentFrameUniform.set(0);

        // Set the time uniform in the shader
        Uniform zoomScaleUniform = Objects.requireNonNull(shader.getUniform("ZoomScale"), "Missing Uniform 'currentFrame'!");
        zoomScaleUniform.set(1.0f);

        // Set the time uniform in the shader
        Uniform cameraYawUniform = Objects.requireNonNull(shader.getUniform("CameraYaw"), "Missing Uniform 'currentFrame'!");
        cameraYawUniform.set(0f);

        // Set the time uniform in the shader
        Uniform cameraPitchUniform = Objects.requireNonNull(shader.getUniform("CameraPitch"), "Missing Uniform 'currentFrame'!");
        cameraPitchUniform.set(0f);

        // Set the time uniform in the shader
        Uniform pixelAlphaUniform = Objects.requireNonNull(shader.getUniform("PixelAlpha"), "Missing Uniform 'currentFrame'!");
        pixelAlphaUniform.set(1f);



        System.out.println("Default uniform 'time' set!");
    }


    private static class CustomRenderTypes extends RenderType {


        private static ShaderInstance babyShader;
        private static ShaderInstance infinityVoidShader;
        private static ShaderInstance infinityVoidSolidShader;
        private static ShaderInstance infinityVoidFullySolidShader;
        private static ShaderInstance infinityVoidWingsShader;

        private static final ShaderStateShard BABY_SHADER_STATE = new ShaderStateShard(() -> babyShader);
        private static final ShaderStateShard INFINITY_VOID_SHADER_STATE = new ShaderStateShard(() -> infinityVoidShader);
        private static final ShaderStateShard INFINITY_VOID_SOLID_SHADER_STATE = new ShaderStateShard(() -> infinityVoidSolidShader);
        private static final ShaderStateShard INFINITY_VOID_WINGS = new ShaderStateShard(() -> infinityVoidWingsShader);



        private CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsOutline, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
            super(name, format, mode, bufferSize, affectsOutline, needsSorting, setupTask, clearTask);
            throw new IllegalStateException("This class should not be instantiated.");
        }




        public static RenderType babyTranslucent(ResourceLocation baseTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(BABY_SHADER_STATE)

                    .setTextureState(new MultiTextureStateShard.Builder()
                            .add(baseTexture, false, false)
                            .build())
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false);

            return RenderType.create(
                    "baby_translucent",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    false,
                    compositeState
            );
        }





        public static RenderType infinity_void(ResourceLocation baseTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(INFINITY_VOID_SHADER_STATE)

                    .setTextureState(new TextureStateShard(baseTexture, false, false))
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);


            return RenderType.create(
                    "infinity_void",
                    DefaultVertexFormat.POSITION_TEX,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    false,
                    compositeState
            );
        }


        public static RenderType infinity_void_solid(ResourceLocation baseTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(INFINITY_VOID_SOLID_SHADER_STATE)
                    .setTextureState(new TextureStateShard(baseTexture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);

            return RenderType.create(
                    "infinity_void_solid",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    true,
                    compositeState
            );
        }


        public static RenderType glowCutout(ResourceLocation texture) {
            return RenderType.create(
                    "glowing_cutout",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    false,
                    true,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderType.RENDERTYPE_EYES_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY) // allows semi-transparent pixels
                            .setWriteMaskState(RenderType.COLOR_WRITE)
                            .setLightmapState(RenderType.NO_LIGHTMAP) // disables ambient lighting
                            .setOverlayState(RenderType.OVERLAY)
                            .createCompositeState(false)
            );
        }






        public static RenderType infinity_void_fully_solid(ResourceLocation baseTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(INFINITY_VOID_SOLID_SHADER_STATE) // same shader
                    .setTextureState(new TextureStateShard(baseTexture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY) // <-- actual solid
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);

            return RenderType.create(
                    "infinity_void_fully_solid",
                    DefaultVertexFormat.POSITION_TEX,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    false,
                    compositeState
            );
        }

        public static RenderType infinity_void_wings(ResourceLocation baseTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(INFINITY_VOID_SOLID_SHADER_STATE)

                    .setTextureState(new TextureStateShard(baseTexture, false, false))
                    .setTransparencyState( new RenderStateShard.TransparencyStateShard(
                            "wing_stars_transparency",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFuncSeparate(
                                        GlStateManager.SourceFactor.SRC_ALPHA,
                                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                        GlStateManager.SourceFactor.ONE,
                                        GlStateManager.DestFactor.ZERO
                                );
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }
                    ))
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true);


            return RenderType.create(
                    "infinity_void_wings",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    false,
                    compositeState
            );
        }


            public static RenderType modGuiTextured(ResourceLocation texture) {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_TEX_SHADER) // must match vertex format
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .createCompositeState(true);

            return RenderType.create(
                    "mod_gui_textured",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    786432,
                    false,
                    true,
                    state
            );

        }







    }


}
