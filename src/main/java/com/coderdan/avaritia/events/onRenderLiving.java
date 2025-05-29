package com.coderdan.avaritia.events;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.ModRenderTypes;
import com.coderdan.avaritia.item.custom.ModInfinityArmorItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Avaritia.MOD_ID, value = Dist.CLIENT)
public class onRenderLiving {

    private static final ResourceLocation WINGTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_wing.png");
    private static final ResourceLocation TXTMASK = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/infinity_armor_mask_wings.png");
    private static final ResourceLocation VOIDSTARSTXT = ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "textures/models/armor/cosmic_0.png");
    private static float time = 0;

    @SubscribeEvent
    public static void onRenderWings(RenderLivingEvent.Post<?,?> event) {

    }
}
