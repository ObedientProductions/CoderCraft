package com.coderdan.avaritia;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.sun.jna.platform.win32.OpenGL32Util;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ModRenderTypes {

    private static ResourceLocation TXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing.png");



    public static ShaderInstance babyShader;
    public static ShaderInstance infinityVoidShader;

    public static RenderType babyShader(ResourceLocation txt, ResourceLocation txtmask) {

        return CustomRenderTypes.babySolid(txt, txtmask);
    }

    public static RenderType infinityVoidShader(ResourceLocation txt) {

        return CustomRenderTypes.infinityVoid(txt);
    }

    public static RenderType modGui(ResourceLocation txt) {

        return CustomRenderTypes.modGui(txt);
    }


    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Avaritia.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":babyshader"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.babyShader = shader;
                ModRenderTypes.babyShader = shader;
                setDefaultUniformTime(shader);

                System.out.println("Baby shader registered and sampler set!");
            });

            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.parse(Avaritia.MOD_ID + ":infinity_void"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                CustomRenderTypes.infinityVoidShader = shader;
                ModRenderTypes.infinityVoidShader = shader;
                setDefaultUniformsInfinityVoid(shader);

                System.out.println("Infinity void shader registered and sampler set!");
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



        System.out.println("Default uniform 'time' set!");
    }

    private static class CustomRenderTypes extends RenderType {


        private static ShaderInstance babyShader;
        private static ShaderInstance infinityVoidShader;

        private static final ShaderStateShard BABY_SHADER_STATE = new ShaderStateShard(() -> babyShader);
        private static final ShaderStateShard INFINITY_VOID_SHADER_STATE = new ShaderStateShard(() -> infinityVoidShader);



        private CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsOutline, boolean needsSorting, Runnable setupTask, Runnable clearTask) {
            super(name, format, mode, bufferSize, affectsOutline, needsSorting, setupTask, clearTask);
            throw new IllegalStateException("This class should not be instantiated.");
        }




        public static RenderType babyTranslucent(ResourceLocation baseTexture, ResourceLocation maskTexture) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                    .setShaderState(BABY_SHADER_STATE)

                    .setTextureState(new MultiTextureStateShard.Builder()
                            .add(baseTexture, false, false)
                            .add(maskTexture, false, false)
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
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(false);


            return RenderType.create(
                    "infinity_void",
                    DefaultVertexFormat.POSITION,
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


        private static RenderType babySolid(ResourceLocation texture, ResourceLocation textureMask) {

            return babyTranslucent(texture, textureMask);
        }

        private static RenderType infinityVoid(ResourceLocation texture) {

            return infinity_void(texture);
        }

        private static RenderType modGui(ResourceLocation texture) {

            return modGuiTextured(texture);
        }





    }


}
