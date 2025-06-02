package com.coderdan.avaritia.item.armor;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

import static com.coderdan.avaritia.item.armor.ModInfinityArmorModel.createMeshes;

public class ArmorModelLayers {

    public static final ModelLayerLocation INFINITY_ARMOR = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "infinity_armor"), "main");

    public static final ModelLayerLocation INFINITY_ARMOR_LEGS = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "infinity_armor_legs"), "main");


    public static final ModelLayerLocation INFINITY_ARMOR_FLAT = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "infinity_armor_flat"), "main");


    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(INFINITY_ARMOR, () -> ModInfinityArmorModel.createBodyLayer(false));
        event.registerLayerDefinition(INFINITY_ARMOR_LEGS, () -> ModInfinityArmorModel.createBodyLayer(true));
        event.registerLayerDefinition(INFINITY_ARMOR_FLAT, ModInfinityArmorModel::createBodyLayerFlat);
    }

}
